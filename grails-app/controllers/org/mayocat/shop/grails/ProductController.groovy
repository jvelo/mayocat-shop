package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException

import org.mayocat.shop.viewmodel.builder.ProductViewModelBuilder

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class ProductController extends AbstractExposedController {

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

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def expose() {
      def product = Product.findByByname(params.byname);
      if (!product) {
        redirect(uri: '/notFound')  
      }
      def builder = new ProductViewModelBuilder()
      render(view:"Product.html", model: [product:builder.build(product)])
    }

    def beforeSave = {
      params.byname = bynameNormalizerService.normalize(params.title)
      params.exposed = true
    }

    def beforeInterceptor = [action:this.&beforeSave, only: ['save']]
    def afterInterceptor = [action:super.afterExpose, only: ['expose']]
}
