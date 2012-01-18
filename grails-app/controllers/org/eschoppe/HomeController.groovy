package org.eschoppe

class HomeController extends AbstractViewModelController {

  static allowedMethods = [expose:"GET"]

  def expose() {
  
    def products = Product.allExposed;
    def categories = Category.findAll();
    def cvms = []
    def pvms = []
    for (category in categories) {
      cvms.add(getCategoryViewModel(category))
    }
    for (product in products) {
      pvms.add(getProductViewModel(product))
    }
    render(view:"Home.html", model: [products:pvms, categories:cvms])
  }

}
