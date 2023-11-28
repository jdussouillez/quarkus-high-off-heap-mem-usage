#!/usr/bin/env bash

########
# Entrypoint of the OCI container. This script is executed by the container when starting
# It is executed in /work directory, which is the result of Maven compilation (target/quarkus-app).
########

NAME=client
JMX_PORT=1329

QUARKUS_DEFAULT_OPTS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
JMX_OPTS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
JFR_OPTS="-XX:StartFlightRecording=filename=/tmp/$NAME.jfr"
JAVA_OPTS="$JMX_OPTS $JFR_OPTS $QUARKUS_DEFAULT_OPTS $JAVA_OPTS"

java $JAVA_OPTS -jar quarkus-run.jar "$@"
