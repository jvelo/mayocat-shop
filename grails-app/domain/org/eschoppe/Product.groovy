package org.eschoppe

class Product {

    static belongsTo = Category
    static hasMany = [categories:Category]

    static constraints = {
    }

    String byname
    String title
    Float price

}
