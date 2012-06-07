package org.mayocat.shop.grails

class Shop {

    // General preferences
    // -------------------

    String name
    String storefront

    // Product preferences
    // -------------------

    // Are the products sold "one-offs" ?
    Boolean singleUnitProducts

    // Keep a track of products stocks ?
    Boolean trackInventory

    // When a product is sold out, continue selling it ?
    Boolean sellWhenSoldOut

    // Is sent by mail ?
    Boolean sentBySnailMail

    // List of payment methods
    List<PaymentMethod> paymentMethod

    // Manage package dimensions/weight ?
    static hasOne = [packageManagement: PackageManagement, checkoutPages: CheckoutPages]
    
    static hasMany = [paymentMethod: PaymentMethod]

    // Categories preferences
    // ----------------------

    // Number of product per page in a category
    Integer categoryProductsPerPage

    /////////////////////////////////////////////////////////////////

    static constraints = {
      name nullable:true
      storefront nullable:true
      categoryProductsPerPage nullable:true
    }

    def beforeValidate() {
      // Sets some defaults if needed
      if (singleUnitProducts == null) {
        singleUnitProducts = false
      }
      if (trackInventory == null) {
        trackInventory = false
      }
      if (sellWhenSoldOut == null) {
        sellWhenSoldOut = false
      }
      if (sentBySnailMail == null) {
        sentBySnailMail = false
      }
    }

}
