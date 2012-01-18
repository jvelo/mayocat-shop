package org.eschoppe

import org.springframework.dao.DataIntegrityViolationException

class ProductController extends AbstractViewModelController {

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
      def productViewModel = getProductViewModel(product)
      render(view:"Product.html", model: [product:productViewModel])
    }

    def beforeInterceptor = [action:this.&beforeSave, only: ['save']]

    def beforeSave = {
      params.byname = bynameNormalizerService.normalize(params.title)
      params.exposed = true
    }
}
