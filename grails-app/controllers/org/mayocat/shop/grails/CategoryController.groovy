package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException

import org.mayocat.shop.viewmodel.builder.CategoryViewModelBuilder

class CategoryController {

    def bynameNormalizerService  // injected

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static navigation = [
      order : 100,
      action : "list",
      title : "Categories",
      path : "category",
      group: 'catalogue'
    ]
    
    static scaffold = true

    def expose() {
      // Browsed category
      def category = Category.findByByname(params.byname)
      if (!category) {
        redirect(uri: '/notFound')
      }
      def builder = new CategoryViewModelBuilder()
      render(view:"/storefronts/lea/Category.html", model: [category:builder.build(category)])
    }

    def beforeInterceptor = [action:this.&beforeSave, only: ['save']]

    def beforeSave = {
      params.byname = bynameNormalizerService.normalize(params.title)
    }
}
