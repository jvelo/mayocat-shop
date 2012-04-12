package org.mayocat.shop.grails

class Page {

    String byname
    String title
    String content

    static constraints = {
      byname unique:true, matches:"[a-zA-Z0-9]+[a-zA-Z0-9\\-]*[a-zA-Z0-9]+", display:false, editable:false
    }

    static mapping = {
      content type:'text'
    }

}
