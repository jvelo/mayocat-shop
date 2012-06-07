package org.mayocat.shop.grails

import org.mayocat.shop.payment.HandlebarsExecutor
import org.springframework.dao.DataIntegrityViolationException
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import groovy.json.JsonSlurper

@Secured(['ROLE_ADMIN'])
class ShopController {

    // Injected
    def paymentGatewayManagerService

    static scaffold = true

    static allowedMethods = [save: "POST", update: "POST"]

    static navigation = [
        [
            group:'main',
            action:'edit',
            title:'Preferences',
            order: 10,
            path : "preferences"
        ],
        [
            group:'submenu:preferences',
            action:'editCheckoutPages',
            title:'Checkout pages',
            order: 20,
            path : "checkout"
        ],
        [
            group:'submenu:preferences',
            action:'editPaymentMethods',
            title:'Payments methods',
            order: 20,
            path : "payments"
        ],
        [
            group:'submenu:preferences',
            action:'edit',
            title:'General',
            order: 10,
            path : "general"
        ]
    ]

    def save() {
        def shopInstance = new Shop(params)
        shopInstance.packageManagement = new PackageManagement()
        shopInstance.checkoutPages = new CheckoutPages()
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
            shopInstance.save(flush:true)
          }
        }

        [shopInstance: shopInstance]
    }

    def editPaymentMethods() {
      edit()
    }

    def configurePaymentMethod() {
        if (!paymentGatewayManagerService.isConfigurable(params.id)) {
            response.status = 403
            render "Payment method ${params.id} is not configurable"
            return
        }
        def paymentMethod = PaymentMethod.findByTechnicalName(params.id)
        def gateway = paymentGatewayManagerService.getGateway(params.id)
        def entity = paymentGatewayManagerService.getOrCreateEntity(params.id)
        def slurper = new JsonSlurper()
        def configuration = slurper.parseText(entity.json)
        def validationErrors = null
        def templateContent = paymentGatewayManagerService.getTemplateContents(params.id, "configure")
        def executor = HandlebarsExecutor.getInstance()
        
        if (request.post) {
            // Save new configuration if valid. Report errors if not.
            def json = request.reader.text
            def newConfiguration = slurper.parseText(json)
            validationErrors = gateway.validateConfiguration(newConfiguration)
            if (validationErrors == null || validationErrors.keySet().size() == 0) {
                entity.json = json
                entity.save()
                
                configuration = newConfiguration
            }   
        }
        
        // Create Handlebars context
        def context = [ configuration: configuration ]
        if (validationErrors != null && validationErrors.keySet().size() > 0) {
          context["errors"] = validationErrors
        }
        
        if (request.post) {
            // Ajax response
            render context as JSON
        }
        else {
            // Display full form
            def form = executor.executeHandlebar(templateContent, context)
            [method: paymentMethod, configurationForm: form, template: templateContent]
        }
    }
    
    def editCheckoutPages() {
        edit()
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

        // Remove empty payment method
        paramsToRemove = []
        for (param in params.keySet()) {
          if (param ==~ /paymentMethod\[.\]\.technicalName/ && params[param] == '') {
            def index = (param =~ /paymentMethod\[(.)\]\.technicalName/)[0][1]
            paramsToRemove.add(index)
          }
        }
        for (param in paramsToRemove) {
          params.remove("paymentMethod[" + param + "]")
          params.remove("paymentMethod[" + param + "].technicalName")
          params.remove("paymentMethod[" + param + "].displayName")
          params.remove("paymentMethod[" + param + "].description")
          params.remove("paymentMethod[" + param + "].className")
          params.remove("paymentMethod[" + param + "].enabled")
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
