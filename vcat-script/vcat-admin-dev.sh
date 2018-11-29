#!/bin/bash
APP_HOME=$(dirname $0)
APP_HOME=${APP_HOME/\./$(pwd)}

JAVA_HOME=/usr/lib/jvm/jdk8
APP_NAME=vcat-admin-dev
APP_PORT=8615

echo "Service [$APP_NAME] - [$1]"

echo "    JAVA_HOME=$JAVA_HOME"
echo "    APP_HOME=$APP_HOME"
echo "    APP_NAME=$APP_NAME"
echo "    APP_PORT=$APP_PORT"

function start {
    if pkill -0 -f $APP_NAME.war > /dev/null 2>&1
    then
        echo "Service [$APP_NAME] is already running. Ignoring startup request."
        exit 1
    fi
    if [ ! -d "logs" ]; then
        mkdir "logs"
    fi
    echo "Starting application..."
    nohup $JAVA_HOME/bin/java -jar $APP_HOME/$APP_NAME.war \
        --spring.config.location=file:$APP_HOME/config/   \
        --server.port=$APP_PORT  \
        --info.info=$APP_NAME    \
        < /dev/null > $APP_HOME/logs/$APP_NAME.log 2>&1 &
}

function stop {
    if ! pkill -0 -f $APP_NAME.war > /dev/null 2>&1
    then
        echo "Service [$APP_NAME] is not running. Ignoring shutdown request."
        exit 1
    fi

    # Wait until the server process has shut down
    attempts=0
    while pkill -0 -f $APP_NAME.war > /dev/null 2>&1
    do
        attempts=$[$attempts + 1]
        if [ $attempts -gt 2 ]
        then
            # We have waited too long. Kill it.
            pkill -f $APP_NAME.war > /dev/null 2>&1
        fi
        sleep 1s
    done
}

case $1 in
start)
    start
;;
stop)
    stop
;;
restart)
    stop
    start
;;
esac
exit 0