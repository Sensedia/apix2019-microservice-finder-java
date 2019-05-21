FROM openjdk:8-jre-alpine
WORKDIR /finder
COPY ./target/apix2019-microservice-finder-java-1.0-SNAPSHOT-full.jar /finder/app.jar
COPY ./src/main/resources/application-conf.json /finder/application-conf.json
CMD ["/usr/bin/java", "-jar", "/finder/app.jar"]


