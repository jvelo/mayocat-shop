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
      def category = Category.findByByname(params.byname)
      if (!category) {
        redirect(uri: '/notFound')
      }
      def categoryViewModel = getCategoryViewModel(category)
      render(view:"Category.html", model: [category:categoryViewModel])
    }
}
