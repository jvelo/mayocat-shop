package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class ShopController {

    static scaffold = true

    static allowedMethods = [save: "POST", update: "POST"]

    static navigation = [
      [
        group:'main',
        action:'edit',
        title:'Preferences',
        order: 10,
        path : "preferences"
      ]
    ]

    def save() {
        def shopInstance = new Shop(params)
        shopInstance.packageManagement = new PackageManagement()
        if (!shopInstance.save(flush: true)) {
            render(view: "create", model: [shopInstance: shopInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'shop.label', default: 'Shop'), shopInstance.id])
        redirect(action: "show", id: shopInstance.id)
    }

    def edit() {
        def shopInstance = Shop.get(params.id)
        if (!shopInstance) {
          shopInstance = Shop.list()[0]
          if (!shopInstance) {
            shopInstance = new Shop(packageManagement: new PackageManagement())
            //shopInstance.packageManagement = new PackageManagement()
            shopInstance.save(flush:true)
          }
        }

        [shopInstance: shopInstance]
    }

    def update() {
        def shopInstance = Shop.get(params.id)
        if (!shopInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shop.label', default: 'Shop'), params.id])
            redirect(action: "edit")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (shopInstance.version > version) {
                shopInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'shop.label', default: 'Shop')] as Object[],
                          "Another user has updated this Shop while you were editing")
                render(view: "edit", model: [shopInstance: shopInstance])
                return
            }
        }

        // Remove empty price rules
        def paramsToRemove = []
        for (param in params.keySet()) {
          if (param ==~ /packageManagement.priceRules\[.\]\.dimension/ && params[param] == '') {
            def index = (param =~ /packageManagement.priceRules\[(.)\]\.dimension/)[0][1]
            paramsToRemove.add(index)
          }
        }
        for (param in paramsToRemove) {
          params.remove("packageManagement.priceRules[" + param + "].dimension")
          params.remove("packageManagement.priceRules[" + param + "].threshold")
          params.remove("packageManagement.priceRules[" + param + "].price")
        }

        bindData(shopInstance, params)

        if (!shopInstance.save(flush: true)) {
            render(view: "edit", model: [shopInstance: shopInstance])
            return
        }

     	  flash.message = message(code: 'admin.preferences.updated', default: 'Shop preferences updated')
        redirect(action: "edit", id: shopInstance.id)
    }

}
