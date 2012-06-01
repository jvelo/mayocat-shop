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
        return getConfigurationResource(id) != null
    }

    def getConfigurationTemplateContents(String id) {
        def file = getConfigurationFile(id)
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

    private def getConfigurationResource(String id) {
        CheckPaymentMethod.class.getClassLoader().getResource("resources/handlebars/payment/" + id + "/configure.html.tpl")
    }
    
    private def getConfigurationFile(String id) {
        new File(this.getConfigurationResource(id).toURI())
    }

    private def getShop() {
        Shop.list()[0]
    }
}
