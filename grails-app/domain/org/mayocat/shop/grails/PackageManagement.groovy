package org.mayocat.shop.grails

class PackageManagement {

    Boolean weight
    Boolean length
    Boolean width
    Boolean height

    Shop shop

    static constraints = {
      weight nullable: true
      length nullable: true
      width nullable: true
      height nullable: true
    }

/*
    def getDimensions() {
      return ["weight", "length", "width", "height"]  
    }
    */
}
