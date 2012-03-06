package org.mayocat.shop.grails

class ImageSet {

  String file
  String filename
  String caption
  String description
  Product product
  Set images

  static belongsTo = [product:Product]
  static hasMany = [images: Image]

  static transients = ['file']

  static constraints = {
    product display:false
    images display:false
    description widget:'textArea', blank:true
  }

  static THUMBNAIL_SIZES = [
    "s" : [ width: 48, height:48 ],
    "m" : [ width: 120, height:120 ]
  ]
}
