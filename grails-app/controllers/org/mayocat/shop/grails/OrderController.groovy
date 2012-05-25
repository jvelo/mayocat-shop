package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class OrderController {

    static allowedMethods = [update: "POST"]

    static navigation = [
      order : 1000,
      action : "list",
      title : "Orders",
      path : "order",
      group: 'main'
    ]
    
    static scaffold = true

    def save() {
      // Voluntary empty to forbid creating orders from the admin.
    }

    def delete() {
      // Voluntary empty to forbid deleting orders from the admin.
    }
}
