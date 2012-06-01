package org.mayocat.shop.grails

import java.util.Currency

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
        this.doPayment(enabledPaymentMethods[0])
      }
      else {
        render(view: "selectPaymentMethod", model: [methods: enabledPaymentMethods])
      }
      
    }
    else {
      render(view: "index.html", model:[template:"checkout", order: order, errors: order.errors.allErrors])
    }
  }


  def doPayment(method) {
    def gateway = paymentGatewayManagerServoce.load(method.className).newInstance()
    if (gateway.hasPrepareStep() && !params.execute) {
    }
    else {
      render(view: "payment", model: [content: gateway.displayExecuteStep()])
    }
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
