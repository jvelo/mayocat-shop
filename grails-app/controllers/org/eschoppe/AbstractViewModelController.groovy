package org.eschoppe

import org.eschoppe.viewmodel.ProductViewModel
import org.eschoppe.viewmodel.CategoryViewModel
import org.eschoppe.viewmodel.ImageViewModel

class AbstractViewModelController {

  def getCategoryViewModel(category) {
    if (!category) {
      return
    }
    def pvms = []
    for (product in category.products) {
      if (product.exposed) {
        pvms.add( getProductViewModel(product) )
      }
    }
    def categoryViewModel = new CategoryViewModel(
      title:category.title,
      url: createLink(controller:'category', action:'expose', params:['byname': category.byname]),
      products: pvms
    )
    categoryViewModel
  }

  def getProductViewModel(product) {
    if (!product) {
      return
    }
    def productViewModel = new ProductViewModel(
      title: product.title,
      price: product.price,
      url: createLink(controller:'product', action:'expose', params: ['byname':product.byname])
    )
    def imagesViewModel = []
    for (image in product.images) {
      imagesViewModel.add(getImageViewModel(image))
    }
    productViewModel.images = imagesViewModel
    productViewModel.featuredImage = getImageViewModel(product.featuredImage)
    productViewModel
  }

  def getImageViewModel(image) {
    if (!image) {
      return
    }
    def thumbnails = [:]
    for (thumbnail in image.images) {
      thumbnails[thumbnail.hint] = createLink(
           controller:'imageSet', 
           action:'expose', 
           params:['imageid':image.id, 'filename':image.filename, 'byname': image.product.byname, 'size':thumbnail.hint]
      )
    }
    new ImageViewModel( 
      caption:image.caption,
      url: createLink(controller:'imageSet', action:'expose', params:['imageid':image.id, 'filename':image.filename, 'byname': image.product.byname]),
      thumbnails: thumbnails
    )
  }

}
