package org.mayocat.shop.grails

import java.util.Currency

class CheckoutController extends AbstractExposedController {

  // Injected
  def shippingPriceCalculatorService

  /**
   * on GET
   */
  def expose() {
    render(view: "index.html", model:[template:"checkout"])
  }

  /**
   * on POST form submit -> try to do an actual checkout
   */
  def exposeDoCheckout() {
    def shop = Shop.list()[0]
    def order = new Order(params)

    if (order.validate()) {
      if (shop.sentBySnailMail) {
        // 1. check if "shop to billing address is checked"
        // 2. copy address or add new address (+validation)

      }
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
      order.save(failOnError: true, flush:true)
      render(view: "index.html", model:[template:"payment"])
    }
    else {
      render(view: "index.html", model:[template:"checkout", order: order, errors: order.errors.allErrors])
    }
  }
  
  def afterInterceptor = [action:super.afterExpose, only: ['expose', 'exposeDoCheckout']]
}
