package org.mayocat.shop.grails

class Entity {

    // Example: payment, transporter, etc.
    String type
    
    // Example: org.mayocat, com.acme, etc.
    String vendor
  
    String name
  
    // The entity content : a serialized JSON object  
    String json
    
    static constraints = {
        json type:'text'
    }
}
