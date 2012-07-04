package org.mayocat.shop.grails

import java.io.IOException
import java.io.File

import grails.gsp.PageRenderer

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
    
    PageRenderer groovyPageRenderer

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

            order.items.each { item ->
              def product = item.product
              if (product && product.stock > 0) {
                product.stock  = product.stock - item.quantity
                if (product.stock == 0) {
                  product.exposed = false  
                }
              }
              product.save()
            }
            
            if (order.status == OrderStatus.PAID) {
              sendOrderValidationEmail(order)
            }
            else if (order.status == OrderStatus.WAITING_FOR_PAYMENT) {
              sendWaitingForPaymentEmail(order)  
            }
            
            return true
        }
        return false
    }

    def sendOrderValidationEmail(Order order) {
        def shopName = Shop.list()[0]?.name
        MessageSource messageSource = applicationContext.getBean("messageSource")
        String mailsubject = messageSource.getMessage("orderValidation.subject", [
            "[${shopName}]"
        ] as Object[], "[${shopName}] Your order validation", LCH.getLocale())
        this.sendOrderRelatedMailToCustomer(order, mailsubject, "/emails/orderConfirmation")
    }

    def sendOrderShippedEmail(Order order) {
        def shopName = Shop.list()[0]?.name
        MessageSource messageSource = applicationContext.getBean("messageSource")
        String mailsubject = messageSource.getMessage("orderShipped.subject", [
            "[${shopName}]"
        ] as Object[], "[${shopName}] Your order has been shipped", LCH.getLocale())
        this.sendOrderRelatedMailToCustomer(order, mailsubject, "/emails/orderShipped")
    }

    def sendWaitingForPaymentEmail(Order order) {
        def shopName = Shop.list()[0]?.name
        MessageSource messageSource = applicationContext.getBean("messageSource")
        String mailsubject = messageSource.getMessage("waitingForPayment.subject", [
            "[${shopName}]"
        ] as Object[], "[${shopName}] Order waiting for payment", LCH.getLocale())
        this.sendOrderRelatedMailToCustomer(order, mailsubject, "/emails/waitingForPayment")
    }

    def sendPaymentAcceptedEmail(Order order) {
        def shopName = Shop.list()[0]?.name
        MessageSource messageSource = applicationContext.getBean("messageSource")
        String mailsubject = messageSource.getMessage("orderShipped.subject", [
            "[${shopName}]"
        ] as Object[], "[${shopName}] Your payment has been accepted", LCH.getLocale())
        this.sendOrderRelatedMailToCustomer(order, mailsubject, "/emails/paymentAccepted")
    }

    private def sendOrderRelatedMailToCustomer(Order order, String mailSubject, String template) {
        def lang = "fr" // FIXME store user language at time of ordering in order.
        def realTemplate = templateExists("${template}_${lang}") ? "${template}_${lang}" : template
        try {
            mailService.sendMail {
                to order.customerEmail
                subject mailSubject
                from Shop.list()[0]?.mailSettings?.fromMail ?: "MayocatShop Mailer<no-reply@mayocatshop.com>"
                body(
                  view: realTemplate,
                  model:[order: order]
                )
            }
        } catch (Exception e) {
          // FIXME try to find a more specific error.
          // java.net.ConnectionException is not caught.
          log.error("Failed to send order related email to customer", e);
        }
    }
    
    ////////////////////////////////////////////////////////////////////

    private def templateExists(String name) {
        groovyPageRenderer.findResource(name) != null
    }

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
