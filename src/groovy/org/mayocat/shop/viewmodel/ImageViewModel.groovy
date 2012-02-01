package org.mayocat.shop.viewmodel

class ImageViewModel {
  
  String url
  String caption
  String description
  Map<String, String> thumbnails

  def withSize(String size) {
    return thumbnails[ thumbnails.keySet().find{ size == it } ]
  }
}
