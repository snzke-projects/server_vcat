#!/bin/bash
APP_HOME=$(dirname $0)
APP_HOME=${APP_HOME/\./$(pwd)}

JAVA_HOME=/usr/lib/jvm/jdk8

echo "Starting log server..."
nohup $JAVA_HOME/bin/java -classpath $APP_HOME/slf4j-api-1.7.21.jar:$APP_HOME/logback-access-1.1.7.jar:$APP_HOME/logback-classic-1.1.7.jar:$APP_HOME/logback-core-1.1.7.jar \
                          ch.qos.logback.classic.net.SimpleSocketServer 6001 $APP_HOME/logback.xml < /dev/null > $APP_HOME/vcat-log-server.log 2>&1 &

