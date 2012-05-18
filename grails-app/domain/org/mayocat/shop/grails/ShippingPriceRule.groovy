package org.mayocat.shop.grails

class ShippingPriceRule {

    String dimension
    float threshold
    BigDecimal price

    static belongsTo = [packageManagement: PackageManagement]

    static constraints = {
    }

}
