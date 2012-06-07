package org.mayocat.shop.grails

class CheckoutPages {

    // Logo image data
    byte[] logo

    // Extra CSS
    String extraCss

    Shop shop

    static constraints = {
        extraCss nullable:true
        logo nullable: true
    }

    static mapping = { extraCss type:'text' }
}
