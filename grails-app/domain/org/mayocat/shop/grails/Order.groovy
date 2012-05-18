package org.mayocat.shop.grails

class Order {

    Address deliveryAddress
    Address billingAddress

    OrderStatus status

    static mapping = {
      /** 'order' is a SQL reserved keyword, so we pluralize the table name to keep the semantic of an order. */
      table 'orders'
    }

    static constraints = {
      billingAddress nullable: true
      deliveryAddress nullable: true
    }

}
