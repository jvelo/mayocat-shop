package org.eschoppe

class Category {

    static hasMany = [products: Product]

    static constraints = {
    }

    String byname
    String title

}
