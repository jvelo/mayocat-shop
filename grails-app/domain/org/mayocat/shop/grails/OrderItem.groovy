package org.mayocat.shop.grails

class OrderItem {
  
    // Copied from a product at the time of the purchase :
    BigDecimal unitPrice
    String description
    String title

    Integer quantity

    // Possible reference on an actual product.
    // Might not be relevant
    Product product
    
    static belongsTo = [order:Order]

    static constraints = {
      unitPrice min: 0.0, scale: 2
      title nullable:false
      description maxSize: 2000, nullable:true
      // Product might be deleted later, it's allright as we have duplicated the important information.
      product nullable:true
    }

    static mapping = {
      product cascade:"save-update" 
    }

}
