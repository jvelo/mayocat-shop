package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException

import org.mayocat.shop.viewmodel.builder.ProductViewModelBuilder

import grails.plugins.springsecurity.Secured
import grails.converters.JSON
import org.mayocat.shop.viewmodel.builder.ProductViewModelBuilder
import org.mayocat.shop.grails.Product

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
      render(view: "index.html", model: [template: "product", product:builder.build(product)])
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def all() {
      // All products
      def productsViewModel = [:]
      def products = Product.findAll(sort:"dateCreated", order:"desc", max:params.number ?: 20, offset: params.offset ?: 0) {
        exposed == true  
        // TODO -> check if has at least one image ?
      }
      def productBuilder = new ProductViewModelBuilder()
      for (product in products) {
        productsViewModel[product.byname] = productBuilder.build(product)
      }
      render productsViewModel as JSON  
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

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        if (params.exposed) {
          [productInstanceList: Product.list(params), productInstanceTotal: Product.count()]
        }
        else {
          def results = Product.createCriteria().list(params) {
            eq("exposed", true)
          }
          [productInstanceList:results.list, productInstanceTotal: results.totalCount ]
        }
    }

    def delete() {
        def productInstance = Product.get(params.id)
        if (!productInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "list")
            return
        }

        try {
            // First remove the product form all its categories
            for (Category c : productInstance.categories) {
                c.removeFromProducts(productInstance)
                c.save(flush: true)
            }
            
            // Then actually delete the product
            productInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    def beforeInterceptor = [action:this.&beforeSave, only: ['save']]
    def afterInterceptor = [action:super.afterExpose, only: ['expose']]

}
