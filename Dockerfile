FROM openjdk:8-jre-alpine
WORKDIR /finder
COPY ./target/apix2019-microservice-finder-java-1.0-SNAPSHOT-full.jar /finder/app.jar
COPY ./src/main/resources/application-conf.json /finder/application-conf.json
COPY ./src/main/resources/log4j2.json /finder/log4j2.json
CMD ["/usr/bin/java","-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4j2LogDelegateFactory", "-Dlog4j.configurationFile=/finder/log4j2.json", "-jar", "/finder/app.jar"]


