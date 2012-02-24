package org.mayocat.shop.grails

import org.springframework.web.servlet.ModelAndView
import org.apache.commons.lang3.StringUtils

class AbstractExposedController {

  static afterExpose = { model, modelAndView ->
    def viewName = StringUtils.substringAfterLast(modelAndView.getViewName(), "/")
    def storefront = Shop.list()[0] ? Shop.list()[0].storefront : ""
    if (!storefront || storefront == "") {
      storefront = "default"  
    }
    modelAndView.viewName = "/storefronts/${storefront}/${viewName}"
  }

}
