package org.mayocat.shop.grails

class Product {

    String byname
    String title
    Float price
    Set images
    ImageSet featuredImage
    Boolean exposed
    Set categories

    static belongsTo = Category
    static hasMany = [categories:Category, images:ImageSet]

    static constraints = {
      byname unique:true, matches:"[a-zA-Z0-9]+[a-zA-Z0-9\\-]*[a-zA-Z0-9]+", display:false, editable:false
      title blank:false
      price nullable:true, min:0 as Float
      featuredImage nullable:true
    }

    static allExposed = where {
      exposed == true
    }
}
