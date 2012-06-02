package org.mayocat.shop.grails

import groovy.json.JsonSlurper
import java.util.Currency

import org.mayocat.shop.payment.HandlebarsExecutor
import org.mayocat.shop.payment.CheckPaymentMethod

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
        this.doBeforePayment(enabledPaymentMethods[0].technicalName)
      }
      else {
        render(view: "selectPaymentMethod", model: [methods: enabledPaymentMethods])
      }
      
    }
    else {
      render(view: "index.html", model:[template:"checkout", order: order, errors: order.errors.allErrors])
    }
  }


  def doBeforePayment(method) {
    if (!method) {
      method = params.method     
    }
    def paymentMethod = PaymentMethod.findByTechnicalName(method)
    def gateway = paymentGatewayManagerService.getGateway(method)
    def entity = paymentGatewayManagerService.getOrCreateEntity(method)
    def slurper = new JsonSlurper()
    def configuration = slurper.parseText(entity.json)
    def executor = HandlebarsExecutor.getInstance()

    def context = [
      configuration: configuration,
      order: [:]    
    ]
    
    //if (gateway.hasPrepareStep() && !params.execute) {
        def templateContent = paymentGatewayManagerService.getTemplateContents(method, "before")
        def beforeContent = executor.executeHandlebar(templateContent, context)        
        render(view: "payment", model: [method: paymentMethod, beforeContent: beforeContent])
  }

  
  def doPayment() {
      println "Do payment"
      render "OK"
  }

  def prepareOrder(shop, order) {
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

  def selectPaymentMethod() {
    def order = Order.get(session["order"])
  }
  
  def afterInterceptor = [action:super.afterExpose, only: ['expose']]
}
