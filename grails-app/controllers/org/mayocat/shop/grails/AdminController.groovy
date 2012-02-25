package org.mayocat.shop.grails

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class AdminController {

  static allowedMethods = [dashboard:"GET"]

  def dashboard() {
    render(view:'dashboard.gsp')  
  }
}
