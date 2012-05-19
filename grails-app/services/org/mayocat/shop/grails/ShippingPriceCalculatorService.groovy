package org.mayocat.shop.grails

class ShippingPriceCalculatorService {

  def calculate(shop, cart) { 
    if (!shop.sentBySnailMail || shop.packageManagement == null) {
      return null
    }
    else {
      def rules = shop.packageManagement.priceRules
      def weight = 0
      def price = 0
      def shipping = null
      for (productByname in cart.keySet()) {
        def product = Product.findByByname(productByname)
        weight += getWeight(product)
        price += product.price
      }
      for (rule in rules) {
        if (
              (rule.dimension == 'weight' && weight > rule.threshold) 
              || (rule.dimension == 'price' && price > rule.threshold)
          ) {
          shipping = rule.price
        }
      }
      return shipping
    }
  }

  def getWeight(product) {
    for (dimension in product.packageDimensions) {
      if (dimension.type == 'weight') {
        return dimension.value
      }
    }
    return 0
  }

}
