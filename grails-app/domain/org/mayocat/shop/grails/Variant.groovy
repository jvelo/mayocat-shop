package org.mayocat.shop.grails

class Variant {

    // Is the variant exposed in store ?
    Boolean exposed

    // The price for this variant
    // When null, the parent product price is applied
    BigDecimal price

    // The stock keeping unit code for this variant.
    String sku

    // A potential title for this variant
    String title

    // A potential description for this variant
    String description

    // The remaining stock of this product variant
    Integer stock

    static belongsTo = Product

    static constraints = {
      price nullable:true, min:0.0
      title nullable:true
      sku nullable:true
      description nullable:true
      stock nullable:true
    }

    static allExposed = where {
      exposed == true
    }
}
