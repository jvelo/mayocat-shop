package org.eschoppe

class Product {

    String byname
    String title
    Float price
    Set images

    static belongsTo = Category
    static hasMany = [categories:Category, images:ImageSet]

    static constraints = {
      byname unique:true, matches:"[a-zA-Z0-9]+[a-zA-Z0-9\\-]*[a-zA-Z0-9]+", display:false, editable:false
      title blank:false
      price blank:false, min:0 as Float
    }

}
