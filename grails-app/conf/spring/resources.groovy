import org.eschoppe.thymeleaf.*

// Place your Spring DSL code here
beans = {
  
  thymeleafTemplateResolver(org.eschoppe.thymeleaf.EschoppeThymeleafTemplateResolver){
    prefix = '/storefronts/default/'
    suffix = ''
    templateMode = 'HTML5'
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
