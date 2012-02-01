package org.mayocat.shop.grails

class CartController {

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
    render(view: "/storefronts/lea/Cart.html")
  }
  
  def getCart() {
    if (!session['cart']) {
      session['cart'] = [:]
    }
    session['cart']
  }

  // Utility
}
