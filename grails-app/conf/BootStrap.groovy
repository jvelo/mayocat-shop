import org.mayocat.shop.util.DataSourceUtils
import org.mayocat.shop.grails.SecurityUser
import org.mayocat.shop.grails.SecurityRole
import org.mayocat.shop.grails.SecurityUserRole

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
        def testAdmin = new SecurityUser(username: 'admin', enabled: true, password: 'admin')
        testAdmin.save(flush: true)

        // Give user role to user
        SecurityUserRole.create(testAdmin, adminRole, true)
        SecurityUserRole.create(testUser, userRole, true)

        assert SecurityUser.count() == 2
        assert SecurityRole.count() == 2 
        assert SecurityUserRole.count() == 2
      }
    }

    def destroy = {
    }

}
