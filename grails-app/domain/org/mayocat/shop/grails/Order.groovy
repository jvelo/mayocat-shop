package org.mayocat.shop.grails

import java.util.Currency

class Order {

    Address deliveryAddress
    Address billingAddress
    String customerEmail

    OrderStatus status
    List<OrderItem> items

    Date dateCreated
    Date dateUpdated

    BigDecimal totalProducts
    BigDecimal shipping
    BigDecimal grandTotal
    
    Currency currency

    static hasMany = [items:OrderItem]

    static constraints = {
      customerEmail nullable: false
      billingAddress nullable: false
      deliveryAddress nullable: true
      status nullable: true
      shipping nullable: true
      currency nullable: true
    }

    static mapping = {
      // 'order' is a SQL reserved keyword, so we pluralize the table name to keep the semantic of an order.
      table 'orders'

      // Cascade save and update, but don't delete addresses when deleting an order
      // (which actually is not even made possible from the UI)
      deliveryAddress cascade:"save-update"
      billingAddress cascade:"save-update"
    }

    def beforeValidate() {
      if (dateUpdated == null) {
        dateUpdated = new Date()
      }
      if (dateCreated == null) {
        dateCreated = new Date()  
      }
      if (grandTotal == null) {
        grandTotal = 0
      }
      if (shipping == null) {
        shipping = 0
      }
      if (totalProducts == null) {
        totalProducts = 0
      }
    }

    def beforeUpdate() {
      dateUpdated = new Date()
    }


}
