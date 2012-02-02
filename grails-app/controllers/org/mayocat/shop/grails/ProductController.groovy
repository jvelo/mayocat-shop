package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException

import org.mayocat.shop.viewmodel.builder.ProductViewModelBuilder

class ProductController {

    def bynameNormalizerService  // injected

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static scaffold = true

    static navigation = [
      [
        group:'main',
        action:'list',
        title:'Catalogue',
        order: 10,
        path : "product"
      ],
      [
        group:'catalogue',
        action : "list",
        title : "Products",
        order : 10,
        path : "product"
      ]
    ]

    def expose() {
      def product = Product.findByByname(params.byname);
      if (!product) {
        redirect(uri: '/notFound')  
      }
      def builder = new ProductViewModelBuilder()
      render(view:"/storefronts/lea/Product.html", model: [product:builder.build(product)])
    }

    def beforeInterceptor = [action:this.&beforeSave, only: ['save']]

    def beforeSave = {
      params.byname = bynameNormalizerService.normalize(params.title)
      params.exposed = true
    }
}
