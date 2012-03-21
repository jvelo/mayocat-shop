package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException

import org.mayocat.shop.viewmodel.builder.CategoryViewModelBuilder

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class CategoryController extends AbstractExposedController {

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

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def expose() {
      // Browsed category
      def category = Category.findByByname(params.byname)
      if (!category) {
        redirect(uri: '/notFound')
      }
      def builder = new CategoryViewModelBuilder()
      render(view:"index.html", model: [template:"category", category:builder.build(category)])
    }

    def beforeSave = {
      params.byname = bynameNormalizerService.normalize(params.title)
    }

    def beforeInterceptor = [action:this.&beforeSave, only: ['save']]
    def afterInterceptor = [action:super.afterExpose, only: ['expose']]
}
