package org.mayocat.shop.grails

class Shop {

    String name
    String storefront

    static constraints = {
      name nullable:true
      storefront nullable:true
    }

}
