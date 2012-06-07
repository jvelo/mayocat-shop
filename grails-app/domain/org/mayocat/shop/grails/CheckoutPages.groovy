package org.mayocat.shop.grails

class CheckoutPages {

    // Logo image data
    byte[] logo

    // Extra CSS
    String extraCss
    
    String logoExtension
    // A counter to be incremented each time a new logo is uploaded
    Integer logoVersion

    Shop shop

    static constraints = {
        extraCss nullable:true
        logo(maxSize: LOGO_MAX_SIZE, nullable: true)
        logoExtension nullable: true
        logoVersion nullable: true
    }

    static mapping = { extraCss type:'text' }
    
    static final Integer LOGO_MAX_SIZE = 1024 * 1024
}
