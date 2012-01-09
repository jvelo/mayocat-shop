package org.eschoppe

class ImageSet {

  String file
  String caption
  String description
  Image original

  Set thumbnails
  Product product

  static belongsTo = [Product]
  static hasMany = [thumbnails: Image]

  static transients = ['file']

  static constraints = {
    product display:false
    original display:false
    thumbnails display:false
    description widget:'textArea', blank:true
  }

  static THUMBNAIL_SIZES = [
    "s" : [ width: 48, height:48 ],
    "m" : [ width: 120, height:120 ]
  ]
}
