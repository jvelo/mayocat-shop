import org.mayocat.shop.util.DataSourceUtils

class BootStrap {

    def grailsApplication 
    def sessionFactory 

    def init = { servletContext ->
      // Prevent mysql broken pipe
      DataSourceUtils.tune(servletContext)

      // 
      grailsApplication.controllerClasses.each { 
        def oldRedirect = it.metaClass.redirect 
        it.metaClass.redirect = { args -> 
           sessionFactory.currentSession.flush() 
           oldRedirect(args) 
        } 
      } 
    }
    def destroy = {
    }

}
