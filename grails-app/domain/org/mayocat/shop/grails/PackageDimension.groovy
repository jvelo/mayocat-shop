package org.mayocat.shop.grails

class PackageDimension {

    String type
    Float value
    String unit

    static belongsTo = [product: Product]

    static constraints = {
      value nullable: true
      unit nullable: true
    }

}
