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

    def editCategories() {
      def productInstance = Product.get(params.id)
      if (!productInstance) {
          flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
          redirect(action: "list")
          return
      }
      [productInstance: productInstance, categories:Category.findAll()]
    }

    def updateCategories() {
      def productInstance = Product.get(params.id)

      if (!params.categories) {
        params.categories = []
      }

      // Remove some categories ?
      for (c in productInstance.categories) {
        if (!(params.categories as List).contains(c.id as String)) {
          c.products.removeAll(productInstance)
          c.save(flush:true)
        }
      }

      // Add some new categories ?
      for (c in params.categories) {
        def category = Category.get(c as Integer)
        if (!category.products.contains(productInstance)) {
          category.addToProducts(productInstance)
          category.save(flush:true)
        }
      }
      redirect(action:"show", id: productInstance.id, fragment:"Categories")
    }

    def beforeInterceptor = [action:this.&beforeSave, only: ['save']]
    def afterInterceptor = [action:super.afterExpose, only: ['expose']]

}
