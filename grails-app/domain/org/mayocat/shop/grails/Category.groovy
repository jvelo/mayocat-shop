package org.mayocat.shop.grails

class Category {

    static hasMany = [products: Product]

    static constraints = {
      byname unique:true, matches:"[a-zA-Z0-9]+[a-zA-Z0-9\\-]*[a-zA-Z0-9]+", display:false, editable:false
    }

    String byname
    String title

}
