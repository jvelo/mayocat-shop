package org.eschoppe

class Image {

  Integer width
  Integer height
  String extension
  String hint
  byte[] data

  static belongsTo = [ImageSet]
  
  static constraints = {
    data(maxSize: MAX_SIZE)
    width nullable:true
    height nullable:true
    extension nullable:true
  }

  static final Integer MAX_SIZE = 2 * 1024 * 1024
}
