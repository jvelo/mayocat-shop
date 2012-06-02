package org.mayocat.shop.grails

import java.io.File

import org.mayocat.shop.payment.CheckPaymentMethod

import com.google.common.base.Charsets
import com.google.common.io.Files

class PaymentGatewayManagerService {

    def getOrCreateEntity(String id) {
        def method = getShop().paymentMethod.find { it.technicalName == id }
        def entity = Entity.findByTypeAndName("payment", method.technicalName)
        if (!entity) {
            entity = new Entity(
                    type: 'payment',
                    vendor: 'Mayocat',
                    name: method.technicalName,
                    json: "{}"
                    )
            entity.save()
        }
        return entity
    }

    def isConfigurable(String id) {
        return getTemplateResource(id, "configure") != null
    }

    def getTemplateContents(String id, String template) {
        def file = getTemplateFile(id, template)
        if (!file) {
            return null
        }
        Files.toString(file,  Charsets.UTF_8)
    }
    
    def getGateway(String id) {
        def method = getShop().paymentMethod.find { it.technicalName == id }
        this.load(method.className).newInstance()
    }

    def load(String className) {
        Thread.currentThread().contextClassLoader.loadClass(className)
    }

    private def getTemplateResource(String id, String template) {
        CheckPaymentMethod.class.getClassLoader().getResource("resources/handlebars/payment/" + id + "/" + template + ".html.tpl")
    }
    
    private def getTemplateFile(String id, String template) {
        new File(this.getTemplateResource(id, template).toURI())
    }

    private def getShop() {
        Shop.list()[0]
    }
}
