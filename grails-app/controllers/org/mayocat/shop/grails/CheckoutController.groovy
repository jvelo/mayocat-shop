package org.mayocat.shop.grails

import groovy.json.JsonSlurper
import java.util.Currency

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
    def expose() {
        render(view: "index.html", model:[template:"checkout"])
    }

    /**
     * on POST form submit -> try to do an actual checkout
     */
    def createOrder() {
        def shop = Shop.list()[0]
        def order = new Order(params)

        if (order.validate()) {
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
            render(view: "index.html", model:[template:"checkout", order: order, errors: order.errors.allErrors])
        }
    }


    def doBeforePayment() {
        def order = Order.get(session["order"])
        def method = params.method
        def paymentMethod = PaymentMethod.findByTechnicalName(method)
        def gateway = paymentGatewayManagerService.getGateway(method)
        def entity = paymentGatewayManagerService.getOrCreateEntity(method)
        def slurper = new JsonSlurper()
        def configuration = slurper.parseText(entity.json)
        def executor = HandlebarsExecutor.getInstance()

        def data = gateway.prepareBeforePayment(order, configuration)

        def context = [
                    configuration: configuration,
                    order: [:],
                    data: data
                ]

        def templateContent = paymentGatewayManagerService.getTemplateContents(method, "before")
        def beforeContent = executor.executeHandlebar(templateContent, context)
        render(view: "payment", model: [method: paymentMethod, beforeContent: beforeContent, hasExternalForm: gateway.hasExternalForm()])
    }


    def doPaymentSuccess() {
        def order = Order.get(session["order"])
        println "SUCCESS !"
        // TODO
    }

    def doPaymentAck() {
        if (!params.method) {
            response.status = 403
            return "NOT OK"
        }
        def (paymentMethod, gateway, configuration) = retrievePaymentMethod(params.method)

        def ackResponse = gateway.acknowledgePayment(getFlatMap(request.parameterMap), configuration)
        paymentGatewayManagerService.processPaymentResponse(paymentMethod, ackResponse)
        response.setContentType(ackResponse.getResponseContentType)
        println "About to respond: " + ackResponse.getResponseContent()
        render ackResponse.getResponseContent()
    }

    def selectPaymentMethod() {
        def order = Order.get(session["order"])
    }

    private def getFlatMap(servletParametersMap) {
        def result = [:]
        for (Map.Entry<String,String[]> entry : servletParametersMap.entrySet()) {
            String[] v = entry.getValue()
            Object o = (v.length == 1) ? v[0] : v
            result.put(entry.getKey(), o)
        }
        result
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
