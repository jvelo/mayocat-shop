package org.eschoppe

class HomeController {

  static allowedMethods = [expose:"GET"]

  def expose() {
    render(view:"/storefronts/lea/Home.html")
  }

}
