package org.mayocat.shop.grails

class CheckoutPages {

    // Extra CSS
    String extraCss
    
    String logoExtension
    // A counter to be incremented each time a new logo is uploaded
    Integer logoVersion
    // Logo image data
    byte[] logo

    String backgroundExtension
    Integer backgroundVersion
    byte[] background
    
    Shop shop

    static constraints = {
        extraCss nullable:true
        
        logo(maxSize: LOGO_MAX_SIZE, nullable: true)
        logoExtension nullable: true
        logoVersion nullable: true
        
        background(maxSize: BG_MAX_SIZE, nullable: true)
        backgroundExtension nullable: true
        backgroundVersion nullable: true
    }

    static mapping = { extraCss type:'text' }
    
    static final Integer LOGO_MAX_SIZE = 1024 * 1024
    static final Integer BG_MAX_SIZE = 2 * 1024 * 1024
}
