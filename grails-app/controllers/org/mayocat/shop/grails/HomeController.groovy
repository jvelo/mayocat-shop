package org.mayocat.shop.grails

class HomeController {

  static allowedMethods = [expose:"GET"]

  def expose() {
    render(view:"/storefronts/lea/Home.html")
  }

}
