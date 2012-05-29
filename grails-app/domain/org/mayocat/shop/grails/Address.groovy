package org.mayocat.shop.grails

class Address {

    String company
    String firstName
    String lastName
    String address
    String address2
    String zip
    String city
    String countryCode

    String phone
    String phone2

    /** Ex. code number, floor, etc. */
    String extraInformation

    static mapping = {
      extraInformation type:'text'
    }

    static constraints = {
      company nullable: true
      address2 nullable: true
      countryCode nullable: true
      phone nullable: true
      phone2 nullable: true
    }
}
