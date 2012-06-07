import org.mayocat.shop.util.DataSourceUtils
import org.mayocat.shop.grails.CheckoutPages
import org.mayocat.shop.grails.SecurityUser
import org.mayocat.shop.grails.SecurityRole
import org.mayocat.shop.grails.SecurityUserRole
import org.mayocat.shop.grails.Shop
import org.mayocat.shop.grails.PackageManagement

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

      if (SecurityUser.count() == 0) {
        // Add admin
        def adminRole = new SecurityRole(authority: 'ROLE_ADMIN').save(flush: true)
        def userRole = new SecurityRole(authority: 'ROLE_USER').save(flush: true)

        def testUser = new SecurityUser(username: 'user', enabled: true, password: 'password')
        testUser.save(flush: true)
        def testAdmin = new SecurityUser(username: 'Admin', enabled: true, password: 'admin')
        testAdmin.save(flush: true)

        // Give user role to user
        SecurityUserRole.create(testAdmin, adminRole, true)
        SecurityUserRole.create(testUser, userRole, true)

        assert SecurityUser.count() == 2
        assert SecurityRole.count() == 2 
        assert SecurityUserRole.count() == 2
      }

      // Migrations 
      this.ensureShopHasPackageManagement();
      this.ensureShopHasCheckoutPagesPreferences();
    }

    def ensureShopHasPackageManagement = {
      def shop = Shop.list()[0]
      if (shop && shop.packageManagement == null) {
        try {
          def pm = new PackageManagement()
          pm.shop = shop
          shop.packageManagement = pm
          shop.save(flush:true)
        }
        catch (Exception e) {
          def session = sessionFactory.currentSession
          session.setFlushMode(org.hibernate.FlushMode.MANUAL)
          log.error("Failed to add package management. ${org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage(e)}")
        }
      }
    }
    
    def ensureShopHasCheckoutPagesPreferences = {
        def shop = Shop.list()[0]
        if (shop && shop.checkoutPages == null) {
          try {
            def cp = new CheckoutPages()
            cp.shop = shop
            shop.checkoutPages = cp
            shop.save(flush:true)
          }
          catch (Exception e) {
            def session = sessionFactory.currentSession
            session.setFlushMode(org.hibernate.FlushMode.MANUAL)
            log.error("Failed to add checkout pages preferences. ${org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage(e)}")
          }
        }
      }

    def destroy = {
    }

}
