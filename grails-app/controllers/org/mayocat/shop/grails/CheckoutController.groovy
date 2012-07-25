package org.mayocat.shop.grails

import groovy.json.JsonSlurper
import java.util.Currency

import org.mayocat.shop.viewmodel.builder.OrderViewModelBuilder
import org.mayocat.shop.payment.HandlebarsExecutor
import org.mayocat.shop.payment.CheckPaymentGateway

class CheckoutController extends AbstractExposedController {

    // Injected
    def shippingPriceCalculatorService

    // Injected
    def paymentGatewayManagerService

    /**
     * on GET
     */
    def checkout() {
        render("view": "checkout", model: [cart: session["cart"]])
    }

    /**
     * on POST form submit -> try to do an actual checkout
     */
    def createOrder() {
        def shop = Shop.list()[0]
        
        def keepDeliveryAddress = false
        for (param in params.keySet()) {
            if (param ==~ /deliveryAddress.+/ && params[param] != '') {
                keepDeliveryAddress = true
            }
        }
        if (!keepDeliveryAddress) {
            def paramsToRemove = []
            for (param in params.keySet()) {
                if (param ==~ /deliveryAddress.*/ && params[param] != '') {
                    paramsToRemove.add(param)
                }
            }
            for (param in paramsToRemove) {
                params.remove(param)
            }
        }

        def order = new Order(params)
        
        if (order.validate() && order.billingAddress.validate() && (!keepDeliveryAddress || order.deliveryAddress?.validate())) {
            if (shop.sentBySnailMail) {
                // 1. check if "shop to billing address is checked"
                // 2. copy address or add new address (+validation)

            }
            this.prepareOrder(shop, order)
            order.save(failOnError: true, flush:true)
            session["order"] = order.id

            def enabledPaymentMethods = shop.paymentMethod.findAll { it.enabled }
            if (enabledPaymentMethods.size() == 1) {
                params.method = enabledPaymentMethods[0].technicalName
                //this.doBeforePayment(enabledPaymentMethods[0].technicalName)
                this.doBeforePayment()
            }
            else {
                render(view: "selectPaymentMethod", model: [methods: enabledPaymentMethods])
            }

        }
        else {
            render("view": "checkout", model: [cart: session["cart"], order: order, twoAddress: !params.useBillingAddressForDelivery])
        }
    }


    def doBeforePayment() {
        def order = Order.get(session["order"])
        def method = params.method
        order.paymentMethod = method
        def (paymentMethod, gateway, configuration) = retrievePaymentMethod(method)
        def executor = HandlebarsExecutor.getInstance()

        def data = gateway.prepareBeforePayment(order, configuration)

        def context = [
          configuration: configuration,
          order: new OrderViewModelBuilder().build(order),
          data: data
        ]

        def templateContent = paymentGatewayManagerService.getTemplateContents(method, "before")
        def beforeContent = executor.executeHandlebar(templateContent, context)
        render(view: "payment", model: [method: paymentMethod, beforeContent: beforeContent, hasExternalForm: gateway.hasExternalForm()])
    }


    def doPaymentSuccess() {
        def order = Order.get(session["order"])
        def (paymentMethod, gateway, configuration) = retrievePaymentMethod(order.paymentMethod)
        def executor = HandlebarsExecutor.getInstance()
        
        if (!gateway.hasExternalForm()) {
            // If the payment is internally processed (as opposed to third-party processed payemnts)
            // We do the trigger the payment acknowledgement from here (as opposed to acknowledgement POST
            // requests established by third-parties based gateways).
            params.method = order.paymentMethod
            params.skipResponse = true
            params.orderId = order.id
            this.doPaymentAck()
        }
        
        // Reset order and cart state
        session.removeAttribute("order")
        session.removeAttribute("cart")
        
        def context = [
            configuration: configuration,
            order: [:]
        ]
        
        def templateContent = paymentGatewayManagerService.getTemplateContents(paymentMethod.technicalName, "success")
        def successContent = executor.executeHandlebar(templateContent ?: "", context)
        render(view: "success", model: [method: paymentMethod, successContent: successContent])
    }

    def doPaymentAck() {
        if (!params.method) {
            response.status = 403
            return "NOT OK"
        }
        def (paymentMethod, gateway, configuration) = retrievePaymentMethod(params.method)

        def ackResponse = gateway.acknowledgePayment(params, configuration)
        if (paymentGatewayManagerService.processPaymentResponse(paymentMethod, ackResponse)) {
            // Order modified. Send order paid email
        }
        else {
            // Could not find order. Send failure email ?
        }
        if (!params.skipResponse) {
          render(
              text: ackResponse.getResponseContent(),
              contentType: ackResponse.getResponseContentType(),
              encoding: "UTF-8"
          )
        }
    }

    def selectPaymentMethod() {
        def order = Order.get(session["order"])
    }

    private def retrievePaymentMethod(method){
        def paymentMethod = PaymentMethod.findByTechnicalName(method)
        def gateway = paymentGatewayManagerService.getGateway(method)
        def entity = paymentGatewayManagerService.getOrCreateEntity(method)
        def slurper = new JsonSlurper()
        def configuration = slurper.parseText(entity.json)
        [
            paymentMethod,
            gateway,
            configuration
        ]
    }

    private def prepareOrder(shop, order) {
        order.status = OrderStatus.NONE
        def cart = session["cart"]
        def shipping = shippingPriceCalculatorService.calculate(shop, cart)
        def totalProducts = 0
        for (p in cart.keySet()) {
            def product = Product.findByByname(p)
            def quantity = cart[p]
            def item = new OrderItem(
                    unitPrice: product.price,
                    quantity: quantity,
                    title: product.title,
                    description: product.description,
                    product: product
                    )
            totalProducts += product.price
            order.addToItems(item)
        }
        order.totalProducts = totalProducts
        order.shipping = shipping
        order.grandTotal = totalProducts + (shipping ?: 0)
        // TODO Right now the currency is hard-wired to EUR. Later on available currencies
        // will be configurable in the administration.
        order.currency = Currency.getInstance("EUR")
    }

    def afterInterceptor = [action:super.afterExpose, only: ['expose']]
}
