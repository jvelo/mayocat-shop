package org.eschoppe.util

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationAttributes

class DataSourceUtils {

    static final Logger log = Logger.getLogger(DataSourceUtils)

    //
    // fix for the database killing idle connections
    //
    // http://sacharya.com/grails-dbcp-stale-connections/
    //
    private static final ms = 1000 * 15 * 60

    public static tune = { servletContext ->

        def ctx = servletContext.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT)
        ctx.dataSourceUnproxied.with {d ->
            d.setMinEvictableIdleTimeMillis(ms)
            d.setTimeBetweenEvictionRunsMillis(ms)
            d.setNumTestsPerEvictionRun(3)
            d.setTestOnBorrow(true)
            d.setTestWhileIdle(true)
            d.setTestOnReturn(true)
            d.setValidationQuery('select 1')
            d.setMinIdle(1)
            d.setMaxActive(16)
            d.setInitialSize(8)
        }

        if (log.infoEnabled) {
            log.info "Configured Datasource properties:"
            ctx.dataSource.properties.findAll {k, v -> !k.contains('password') }.each {p ->
                log.info "  $p"
            }
        }
    }
}

