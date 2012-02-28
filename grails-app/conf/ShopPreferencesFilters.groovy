import org.mayocat.shop.grails.Shop

class ShopPreferencesFilters {

  def filters = {

    injectShopPreferencesModelFilter(controller:'*', actionExclude:'expose', find:true) {

      after = { viewModel ->
        // Enhancing returned viewModel with shop preferences
        //

        if (!viewModel) {
          viewModel = [:]
        }

        viewModel.shopInstance = Shop.list()[0]
      }

    }

  }

}
