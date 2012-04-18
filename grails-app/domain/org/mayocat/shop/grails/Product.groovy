package org.mayocat.shop.grails

class Product {
  
    String byname
    String title
    Boolean exposed
    BigDecimal price
    Integer stock
    String description

    List<Image> images

    Set categories
    List<PackageDimension> packageDimensions

    List<Variant> variants
    Variant defaultVariant

    Date dateCreated
    Date dateUpdated

    static belongsTo = Category
    static hasMany = [categories:Category, images:ImageSet, variants:Variant, packageDimensions:PackageDimension]

    static constraints = {
      byname unique:true, matches:"[a-zA-Z0-9]+[a-zA-Z0-9\\-]*[a-zA-Z0-9]+", display:false, editable:false
      title blank:false
      price min: 0.0, scale: 2
      defaultVariant nullable:true
      stock nullable:true
      description maxSize: 2000, nullable:true
    }

    static mapping = {
      packageDimensions fetch: 'join'
    }

    static allExposed = where {
      exposed == true
    }

    def beforeValidate() {
      if (!price) {
        price = 0.0  
      }
      if (dateUpdated == null) {
        dateUpdated = new Date()
      }
      if (dateCreated == null) {
        dateCreated = new Date()  
      }
    }

    def beforeInsert() {
      if (Shop.list()[0]?.singleUnitProducts) {
        stock = 1
      }
    }

    def beforeUpdate() {
      dateUpdated = new Date()
    }
}
