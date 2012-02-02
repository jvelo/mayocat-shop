package org.mayocat.shop.grails

class AdminController {

  static allowedMethods = [dashboard:"GET"]

  def dashboard() {
    render(view:'dashboard.gsp')  
  }
}
