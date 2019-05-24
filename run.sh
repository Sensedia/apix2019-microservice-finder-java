#!/bin/bash

mvn clean package

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8005 -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4j2LogDelegateFactory -Dlog4j.configurationFile=log4j2.json -jar ./target/apix2019-microservice-finder-java-1.0-SNAPSHOT-full.jar