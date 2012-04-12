package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

import org.mayocat.shop.viewmodel.builder.PageViewModelBuilder

@Secured(['ROLE_ADMIN'])
class PageController extends AbstractExposedController {

    // Injected name normalizer service
    def bynameNormalizerService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static scaffold = true

    static navigation = [
      [
        group:'main',
        action:'list',
        title:'Pages',
        order: 50,
        path : "Pages"
      ]
    ]

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def expose() {
      // Browsed category
      def page = Page.findByByname(params.byname)
      if (!page) {
        redirect(uri: '/notFound')
      }
      def builder = new PageViewModelBuilder()
      render(view:"index.html", model: [template:"page", page:builder.build(page)])
    }

    def beforeSave = {
      params.byname = bynameNormalizerService.normalize(params.title)
      params.exposed = true
    } 

    def beforeInterceptor = [action:this.&beforeSave, only: ['save']]
    def afterInterceptor = [action:super.afterExpose, only: ['expose']]
}
