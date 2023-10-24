#!/usr/bin/env bash

########
# Entrypoint of the OCI container. This script is executed by the container when starting
# It is executed in /work directory, which is the result of Maven compilation (target/quarkus-app).
########

# Quarkus default opts (use jboss LogManager)
QUARKUS_DEFAULT_OPTS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Java options, concat options defined in JAVA_OPTS env var and Quarkus default opts
JAVA_OPTS="$JAVA_OPTS $QUARKUS_DEFAULT_OPTS"

# Disable shellcheck, we don't want to use $JAVA_OPTS in quotes
# shellcheck disable=SC2086
java $JAVA_OPTS -jar quarkus-run.jar "$@"
