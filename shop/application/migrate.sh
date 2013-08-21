
DEBUG=""
DEBUG_PORT=5005
# DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=$DEBUG_PORT"

java $DEBUG -jar target/mayocat-shop-application-0.9-SNAPSHOT.jar flyway-migrate mayocat.yml
