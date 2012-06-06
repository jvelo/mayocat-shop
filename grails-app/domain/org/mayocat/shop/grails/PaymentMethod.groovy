package org.mayocat.shop.grails

class PaymentMethod {

    String technicalName
    String displayName

    String description

    String className    

    Boolean enabled

    Shop shop

    static belongsTo = [shop: Shop]

    static constraints = {
      description type:'text'
    }

}
