package org.mayocat.shop.grails

class HomeController extends AbstractExposedController {

  static allowedMethods = [expose:"GET"]

  def expose() {
    render(view:"index.html", model:[template : "home"])
  }

  def afterInterceptor = [action:super.afterExpose, only: ['expose']]

}
