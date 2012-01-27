import org.eschoppe.viewmodel.builder.CartViewModelBuilder
import org.eschoppe.viewmodel.builder.CategoryViewModelBuilder
import org.eschoppe.Category

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class ExpositionFilters {

  def filters = {

    expositionFilter(controller:'*', action:'expose') {

      after = { viewModel ->
        // Enhancing returned viewModel with API available to all pages :
        //

        if (!viewModel) {
          viewModel = [:]
        }
        def taglib = new ApplicationTagLib()

        // Cart
        if (session["cart"] == null) {
          session["cart"] = [:]
        }
        viewModel["cart"] = new CartViewModelBuilder().build(session["cart"])

        // Categories
        def categoriesViewModel = [:]
        def categories = Category.findAll();
        def categoryBuilder = new CategoryViewModelBuilder()
        for (category in categories) {
          categoriesViewModel[category.byname] = categoryBuilder.build(category)
        }
        viewModel["categories"] = categoriesViewModel

        // Links
        viewModel["links"] = [
          'home' : taglib.createLink(controller:'home', action:'expose')
         ,'cart' : taglib.createLink(controller:'cart', action:'expose')
         ,'add_to_cart' : taglib.createLink(controller:'cart', action:'add')
         ,'remove_from_cart' : taglib.createLink(controller:'cart', action:'remove')
        ]
      }
    }
  }

}
