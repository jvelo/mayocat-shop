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
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE"
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
        url = 'jdbc:mysql://127.0.0.1/mayocat?zeroDateTimeBehavior=convertToNull'
        dbCreate = 'update'
      }
    }
    test {
        dataSource {
            dbCreate = "update"
        }
    }
}
