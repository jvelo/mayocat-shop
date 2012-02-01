package org.mayocat.shop.viewmodel.builder

import org.mayocat.shop.viewmodel.CategoryViewModel

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class CategoryViewModelBuilder {

  def taglib = new ApplicationTagLib()

  def productViewModelBuilder = new ProductViewModelBuilder()

  def build(category) {
    if (!category) {
      return
    }
    def pvms = []
    for (product in category.products) {
      if (product.exposed) {
        pvms.add( productViewModelBuilder.build(product) )
      }
    }
    def categoryViewModel = new CategoryViewModel(
      byname:category.byname,
      title:category.title,
      url: taglib.createLink(controller:'category', action:'expose', params:['byname': category.byname]),
      products: pvms
    )
    categoryViewModel
  }

}
