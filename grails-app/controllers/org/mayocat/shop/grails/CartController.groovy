package org.mayocat.shop.grails

class CartController extends AbstractExposedController {

  def add() {
    def cart = getCart()
    def product = Product.findByByname(params.product)
    def quantity = params.quantity as Integer
    if (product && quantity > 0) {
      if (cart[product.byname]) {
        cart[product.byname] += quantity
      }
      else {
        cart[product.byname] = quantity
      }
    }
    redirect(action:'expose')
  }


  def remove() {
    def cart = getCart()
    cart.remove(params.product)
    redirect(action:'expose')
  }

  def expose() {
    render(view: "index.html", model:[template:"cart"])
  }
  
  def getCart() {
    if (!session['cart']) {
      session['cart'] = [:]
    }
    session['cart']
  }

  def afterInterceptor = [action:super.afterExpose, only: ['expose']]
}
