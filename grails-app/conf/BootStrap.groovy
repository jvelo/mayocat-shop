import org.eschoppe.util.DataSourceUtils

class BootStrap {

    def init = { servletContext ->
      DataSourceUtils.tune(servletContext)
    }
    def destroy = {
    }
}
