#!/bin/sh

java \
  -classpath "./target/lib/*:./target/mayocat-application-1.0-SNAPSHOT.jar" \
  org.mayocat.shop.application.MayocatShopService \
  server \
  config-mysql.yml

