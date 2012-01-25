package org.eschoppe

class HomeController extends AbstractViewModelController {

  static allowedMethods = [expose:"GET"]

  def expose() {
  
    def products = Product.allExposed;
    def categories = Category.findAll();
    def cvms = [:]
    def pvms = []
    for (category in categories) {
      cvms[category.byname] = getCategoryViewModel(category)
    }
    for (product in products) {
      pvms.add(getProductViewModel(product))
    }
    log.error("CAtegories:" + cvms)
    render(view:"/storefronts/lea/Home.html", model: [products:pvms, categories:cvms])
  }

}
