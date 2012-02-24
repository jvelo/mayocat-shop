import org.mayocat.shop.thymeleaf.*;

beans = {
  
  thymeleafTemplateResolver(
  org.thymeleaf.templateresolver.ServletContextTemplateResolver
  //org.mayocat.shop.thymeleaf.EschoppeThymeleafTemplateResolver
  ){
    prefix = ''
    suffix = ''
    templateMode = 'LEGACYHTML5'
    cacheable = false
  }

  thymeleafTemplateEngine(org.thymeleaf.spring3.SpringTemplateEngine) {
    templateResolver = ref("thymeleafTemplateResolver")
  }

  thymeleafViewResolver(org.thymeleaf.spring3.view.ThymeleafViewResolver) {
    order = 10
    templateEngine = ref("thymeleafTemplateEngine")
    viewNames = "*.html"
  }

}
