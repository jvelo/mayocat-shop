package org.mayocat.shop.viewmodel.builder

import org.mayocat.shop.viewmodel.CategoryViewModel

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class CategoryViewModelBuilder {

  def taglib = new ApplicationTagLib()

  def productViewModelBuilder = new ProductViewModelBuilder()

  def build(category, Integer page = 1) {
    if (!category) {
      return
    }
    def pvms = []
    def itemsPerPage = org.mayocat.shop.grails.Shop.list()[0]?.categoryProductsPerPage ?: 15
    def i=0
    def total=0
    def start = (page - 1) * itemsPerPage
    def end = start + itemsPerPage
    for (product in category.products) {
      if (product && product.exposed) {
        if (i < end) {
          if (i >= start) {
            pvms.add( productViewModelBuilder.build(product) )
          }
          i++
        }
        total++
      }
    }
    def pages = total % itemsPerPage == 0 ? total / itemsPerPage : total / itemsPerPage + 1
    def categoryViewModel = new CategoryViewModel(
      byname:category.byname,
      title:category.title,
      url: taglib.createLink(controller:'category', action:'expose', params:['byname': category.byname]),
      products: pvms,
      totalProducts:total,
      pages:pages,
      currentPage:page,
      productsPerPage:itemsPerPage
    )
    categoryViewModel
  }

}
