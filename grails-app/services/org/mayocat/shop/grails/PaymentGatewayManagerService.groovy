package org.mayocat.shop.grails

import java.io.IOException

import java.io.File

import org.mayocat.shop.payment.CheckPaymentGateway
import org.mayocat.shop.payment.PaymentResponse
import org.springframework.beans.BeansException
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder as LCH

import org.codehaus.jackson.JsonGenerationException
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.SerializationConfig

import com.google.common.base.Charsets
import com.google.common.io.Files

class PaymentGatewayManagerService implements ApplicationContextAware {

    ////////////////////////////////////////////////////////////////////
    
    // Injected beans
    
    def mailService
    
    ////////////////////////////////////////////////////////////////////
    
    private def ApplicationContext applicationContext

    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.applicationContext = context        
    }
    
    ////////////////////////////////////////////////////////////////////
    
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

    def processPaymentResponse(method, PaymentResponse response) {
        def order = Order.get(response.getOrderId())
        def mapper = new ObjectMapper().configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
        def json = "{}"
        try {
            json =  mapper.writeValueAsString(response.getEntityToSave())
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e)
        } catch (JsonMappingException e) {
            throw new RuntimeException(e)
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
        if (order) {
            order.status = response.getNewStatus()
            Payment p = new Payment(
                    type: method.technicalName,
                    date: new Date(),
                    json: json
                    )
            order.addToPayments(p)
            order.save(failOnError: true, flush:true)
            
            sendOrderValidationEmail(order)
            
            return true
        }
        return false
    }

    def sendOrderValidationEmail(Order order) {
        MessageSource messageSource = applicationContext.getBean("messageSource")
        def shopName = Shop.list()[0]?.name
        String mailsubject = messageSource.getMessage("orderValidation.subject", [
            "[${shopName}]"
        ] as Object[], "[${shopName}] Your order validation", LCH.getLocale())
        try {
            mailService.sendMail {
                to order.customerEmail
                subject mailsubject
                from Shop.list()[0]?.mailSettings?.fromMail ?: "MayocatShop Mailer<no-reply@mayocatshop.com>"
                body(
                  view:"/emails/orderConfirmation",
                  model:[order: order]
                )
            }
        } catch (java.net.ConnectException e) {
          log.error("Failed to send order validation email", e);
        }
    }
    
    ////////////////////////////////////////////////////////////////////

    private def load(String className) {
        Thread.currentThread().contextClassLoader.loadClass(className)
    }

    private def getTemplateResource(String id, String template) {
        CheckPaymentGateway.class.getClassLoader().getResource("resources/handlebars/payment/" + id + "/" + template + ".html.tpl")
    }

    private def getTemplateFile(String id, String template) {
        def res = this.getTemplateResource(id, template)?.toURI()
        return res ? new File(res) : null
    }

    private def getShop() {
        Shop.list()[0]
    }

}
