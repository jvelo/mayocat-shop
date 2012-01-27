package org.eschoppe.viewmodel.builder

import org.eschoppe.Product
import org.eschoppe.viewmodel.ProductViewModel
import org.eschoppe.viewmodel.ImageViewModel

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class ProductViewModelBuilder {

  def taglib = new ApplicationTagLib()

  def build(product) {
    if (!product) {
      return
    }
    def productViewModel = new ProductViewModel(
      byname: product.byname,
      title: product.title,
      price: product.price,
      url: taglib.createLink(controller:'product', action:'expose', params: ['byname':product.byname])
    )
    def imagesViewModel = []
    for (image in product.images) {
      imagesViewModel.add( this.buildImage(image) )
    }
    def categories = []
    for (category in product.categories) {
      categories.add(category.byname)
      if (!productViewModel.category) {
        productViewModel.category = category.byname
      }
    }
    productViewModel.images = imagesViewModel
    productViewModel.featuredImage = this.buildImage(product.featuredImage)
    productViewModel.categories = categories
    return productViewModel
  }


  def buildImage(image) {
    if (!image) {
      return
    }
    def thumbnails = [:]
    for (thumbnail in image.images) {
      thumbnails[thumbnail.hint] = taglib.createLink(
           controller:'imageSet', 
           action:'expose', 
           params:['imageid':image.id, 'filename':image.filename, 'byname': image.product.byname, 'size':thumbnail.hint]
      )
    }
    new ImageViewModel( 
      caption:image.caption,
      url: taglib.createLink( controller:'imageSet'
                             ,action:'expose'
                             ,params:['imageid':image.id,'filename':image.filename, 'byname': image.product.byname]),
      thumbnails: thumbnails
    )
  }



}
