package org.eschoppe

class Image {

  // Real image dimensions
  Integer width
  Integer height

  // Crop area relative to original image (optionnal)
  Integer x1
  Integer y1
  Integer x2
  Integer y2

  // File extension
  String extension

  // Hint associated with this thumbnail. Null for original image
  String hint

  // Image data
  byte[] data

  // Image set this image belongs to
  ImageSet imageSet
  static belongsTo = [ImageSet]
  
  static constraints = {
    data(maxSize: MAX_SIZE)
    width nullable:true
    height nullable:true
    extension nullable:true
    hint nullable:true
    x1 nullable:true
    y1 nullable:true
    x2 nullable:true
    y2 nullable:true
  }

  static final Integer MAX_SIZE = 2 * 1024 * 1024
}
