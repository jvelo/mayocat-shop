package org.mayocat.shop.grails

class Category {

    static hasMany = [products: Product]

    static constraints = {
    }

    String byname
    String title

}
