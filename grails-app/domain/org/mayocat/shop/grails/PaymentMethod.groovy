package org.mayocat.shop.grails

class PaymentMethod {

    String technicalName
    String displayName

    String description

    String className    

    Boolean enabled

    Shop shop
    
    String imageExtension
    Integer imageVersion
    byte[] image

    static belongsTo = [shop: Shop]

    static constraints = {
      description type:'text'
      
      image(maxSize: IMAGE_MAX_SIZE, nullable: true)
      imageExtension nullable: true
      imageVersion nullable: true
    }
    
    static final Integer IMAGE_MAX_SIZE = 1024 * 1024
}
