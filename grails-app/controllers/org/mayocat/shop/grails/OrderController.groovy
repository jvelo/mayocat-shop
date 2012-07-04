package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class OrderController {

    static allowedMethods = [update: "POST"]

    static navigation = [
      order : 1000,
      action : "list",
      title : "orders",
      path : "order",
      group: 'main'
    ]
    
    static scaffold = true

    def paymentGatewayManagerService

    def makeShipped() {
      def order = Order.get(params.id)
      paymentGatewayManagerService.sendOrderShippedEmail(order)
      order.status = OrderStatus.SHIPPED
      order.save()
      redirect(action:'show', id:params.id)
    }

    def acceptPayment() {
      def order = Order.get(params.id)
      paymentGatewayManagerService.sendPaymentAcceptedEmail(order)
      order.status = OrderStatus.PAID
      order.save()
      redirect(action:'show', id:params.id)
    }
    
    def cancelOrder() {
      def order = Order.get(params.id)
      // paymentGatewayManagerService.sendOrderCancelledEmail(order)
      order.status = OrderStatus.CANCELLED
      order.save()

      order.items.each { item ->
        def product = item.product
        product.stock = product.stock + item.quantity
        product.save()
      }
      redirect(action:'show', id:params.id)
    }

    def save() {
      // Voluntary empty to forbid creating orders from the admin.
    }

    def delete() {
      // Voluntary empty to forbid deleting orders from the admin.
    }
}
