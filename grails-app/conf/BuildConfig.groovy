grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        runtime 'mysql:mysql-connector-java:5.1.16'
        compile 'org.thymeleaf:thymeleaf:2.0.2'
        compile 'org.thymeleaf:thymeleaf-spring3:2.0.2'
        compile 'org.apache.commons:commons-lang3:3.1'
        runtime 'net.sourceforge.nekohtml:nekohtml:1.9.15'
        compile 'com.google.guava:guava:12.0'
        runtime 'org.mozilla:rhino:1.7R3'
        compile 'org.codehaus.jackson:jackson-mapper-asl:1.9.7'
        compile 'com.mortennobel:java-image-scaling:0.8.5'
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.5"
        runtime ":lesscss-resources:0.6.1"
        runtime ":navigation:1.3.2"

        build ":tomcat:$grailsVersion"
    }
}
