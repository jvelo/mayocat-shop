package org.mayocat.shop.grails

class ImageSet {

  String file
  String filename
  String caption
  String description
  Set images

  static belongsTo = [Product, Page]
  static hasMany = [images: Image]

  static transients = ['file']

  static constraints = {
    images display:false
    description maxSize:2000, blank:true
  }

  static THUMBNAIL_SIZES = [
    "s" : [ width: 48, height:48 ],
    "m" : [ width: 120, height:120 ]
  ]
}
