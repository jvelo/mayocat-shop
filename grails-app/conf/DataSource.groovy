dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    /*
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE"
        }
    }
    */
    development {
      dataSource {
        dialect = org.hibernate.dialect.MySQLDialect
        driverClassName = 'com.mysql.jdbc.Driver'
        username = 'root'
        password = ''
        url = 'jdbc:mysql://127.0.0.1/lea'
        dbCreate = 'update'
      }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
        }
    }
    production {
      dataSource {
        dialect = org.hibernate.dialect.MySQLDialect
        driverClassName = 'com.mysql.jdbc.Driver'
        username = 'root'
        password = ''
        url = 'jdbc:mysql://127.0.0.1/mayocat'
        dbCreate = 'update'
        /*
        pooled = true
        properties {
           maxActive = -1
           minEvictableIdleTimeMillis=1800000
           timeBetweenEvictionRunsMillis=1800000
           numTestsPerEvictionRun=3
           testOnBorrow=true
           testWhileIdle=true
           testOnReturn=true
           validationQuery="SELECT 1"
        }
        */
      }
    }
    test {
        dataSource {
            dbCreate = "update"
        }
    }
}
