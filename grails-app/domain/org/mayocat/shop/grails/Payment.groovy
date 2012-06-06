package org.mayocat.shop.grails

class Payment {

    // Correspond to the technical name of the payment method. Ex: "check", "paypalexpresscheckout", etc.
    String type

    Date date
  
    // The payment data : a serialized JSON object  
    String json
    
    static constraints = {
        json type:'text'
    }
}
