#!/bin/sh

DEBUG=""
DEBUG_PORT=5005

if [ ! -d "target" ]; then
  echo "You must build first: 'mvn install'"
  exit
fi

JAR_NAME=`ls target | grep "^mayocat-shop" | grep -v postgre`

if [ -z $JAR_NAME ]; then
  echo "You must build first: 'mvn install'"
  exit
fi

if [ "$1" = "debug" ]
then
  DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=$DEBUG_PORT"
fi

export JAVA_OPTS="-server -Xms128m -Xmx512m -XX:MaxPermSize=192m -Dfile.encoding=utf-8 -Djava.awt.headless=true -XX:+UseParallelGC -XX:MaxGCPauseMillis=100"

java $JAVA_OPTS \
  $DEBUG \
  -classpath "../../platform/front/src/main/resources/:../client/src/main/resources/assets/:../themes/src/main/resources/:./target/$JAR_NAME" \
  org.mayocat.shop.application.MayocatShopService \
  server \
  mayocat.yml

