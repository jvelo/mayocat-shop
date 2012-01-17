package org.eschoppe

import org.eschoppe.viewmodel.ProductViewModel
import org.eschoppe.viewmodel.ImageViewModel
import org.springframework.dao.DataIntegrityViolationException

class ProductController {

    def bynameNormalizerService  // injected

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static scaffold = true

    static navigation = [
      action : "list",
      title : "Products",
      order : 10,
      path : "product"
    ]

    def expose() {
      def product = Product.findByByname(params.byname);
      if (!product) {
        redirect(uri: '/notFound')  
      }
      def productViewModel = new ProductViewModel(
        title: product.title,
        price: product.price
      )
      def imagesViewModel = []
      for (image in product.images) {
        imagesViewModel.add(new ImageViewModel( 
          caption:image.caption,
          url: createLink(controller:'imageSet', action:'expose', params:['imageid':image.id, 'filename':image.filename, 'byname': product.byname])
        ))
      }
      productViewModel.images = imagesViewModel
      render(view:"Product.html", model: [product:productViewModel])
    }

    def beforeInterceptor = [action:this.&generateByname, only: ['save']]

    def generateByname = {
      params.byname = bynameNormalizerService.normalize(params.title)
    }
}
