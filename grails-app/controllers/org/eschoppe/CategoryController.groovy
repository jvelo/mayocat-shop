package org.eschoppe

import org.springframework.dao.DataIntegrityViolationException

class CategoryController extends AbstractViewModelController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static navigation = [
      order : 100,
      action : "list",
      title : "Categories",
      path : "category"
    ]
    
    static scaffold = true

    def expose() {
      // Browsed category
      def category = Category.findByByname(params.byname)
      if (!category) {
        redirect(uri: '/notFound')
      }
      def categoryViewModel = getCategoryViewModel(category)
      // Common vars
      def products = Product.allExposed;
      def categories = Category.findAll();
      def cvms = [:]
      def pvms = []
      for (c in categories) {
        cvms[c.byname] = getCategoryViewModel(c)
      }
      for (product in products) {
        pvms.add(getProductViewModel(product))
      }
      render(view:"/storefronts/lea/Category.html", model: [category:categoryViewModel, products:pvms, categories:cvms])
    }
}
