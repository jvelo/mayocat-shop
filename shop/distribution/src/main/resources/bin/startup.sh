#!/bin/sh

DEBUG=""
DEBUG_PORT=5005

# Ensure that the commands below are always started in the directory where this script is
# located. To do this we compute the location of the current script.
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`
cd "$PRGDIR"

if [ "$1" = "debug" ]
then
  DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=$DEBUG_PORT"
fi

export JAVA_OPTS="-server -Xms128m -Xmx512m -XX:MaxPermSize=192m -Dfile.encoding=utf-8 -Djava.awt.headless=true -XX:+UseParallelGC -XX:MaxGCPauseMillis=100"

java $JAVA_OPTS \
  $DEBUG \
  -classpath "../client/assets/:../lib/*" \
  org.mayocat.shop.application.MayocatShopService \
  server \
  "../configuration/mayocat.yml"

