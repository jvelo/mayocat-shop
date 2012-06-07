import org.mayocat.shop.grails.Shop
import org.mayocat.shop.grails.CheckoutPages
import org.mayocat.shop.grails.PackageManagement

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class CheckoutFilters {

  def filters = {

    checkoutFilters(controller:'checkout', action:'*') {

      after = { viewModel ->
        // Enhancing returned viewModel with API available to all checkout pages :
        //

        // The shop instance
        def shop = Shop.list()[0] ?: new Shop(
            packageManagement: new PackageManagement(),
            checkoutPages: new CheckoutPages()
        )
        
        // The extra CSS to insert in all checkout pages
        viewModel["extraCss"] = shop.checkoutPages.extraCss
      }
    }
  }

}
