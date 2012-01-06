package org.eschoppe

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
      render "Hello " + product.title
    }

    def beforeInterceptor = [action:this.&generateByname, only: ['save']]

    def generateByname = {
      params.byname = bynameNormalizerService.normalize(params.title)
    }
}
