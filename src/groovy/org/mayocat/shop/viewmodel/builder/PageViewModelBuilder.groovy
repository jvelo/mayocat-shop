package org.mayocat.shop.viewmodel.builder

import org.mayocat.shop.viewmodel.PageViewModel

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class PageViewModelBuilder {

  def taglib = new ApplicationTagLib()

  def build(page) {
    if (!page) {
      return
    }
    def pageViewModel = new PageViewModel(
      byname:page.byname,
      title:page.title,
      content:page.content,
      url: taglib.createLink(controller:'page', action:'expose', params:['byname': page.byname])
    )
    pageViewModel
  }

}
