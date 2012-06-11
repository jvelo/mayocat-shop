package org.mayocat.shop.viewmodel.builder

import org.mayocat.shop.grails.Product
import org.mayocat.shop.viewmodel.ProductViewModel
import org.mayocat.shop.viewmodel.ImageViewModel

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
      description: product.description ?: "",
      price: product.price,
      url: taglib.createLink(controller:'product', action:'expose', params: ['byname':product.byname])
    )
    def imagesViewModel = []
    for (image in product.images) {
      imagesViewModel.add( this.buildImage(image, product) )
    }
    def categories = []
    for (category in product.categories) {
      categories.add(category.byname)
      if (!productViewModel.category) {
        productViewModel.category = category.byname
      }
    }
    productViewModel.images = imagesViewModel
    if (imagesViewModel.size() > 0) {
      productViewModel.featuredImage = imagesViewModel[0]
    }
    productViewModel.categories = categories
    return productViewModel
  }


  def buildImage(image, product) {
    if (!image) {
      return
    }
    def thumbnails = [:]
    for (thumbnail in image.images) {
      thumbnails[thumbnail.hint] = taglib.createLink(
           controller:'imageSet', 
           action:'expose', 
           params:['imageid':image.id, 'filename':image.filename, 'byname': product.byname, 'size':thumbnail.hint]
      )
    }
    new ImageViewModel( 
      caption:image.caption,
      url: taglib.createLink( controller:'imageSet'
                             ,action:'expose'
                             ,params:['imageid':image.id,'filename':image.filename, 'byname': product.byname]),
      thumbnails: thumbnails
    )
  }



}
