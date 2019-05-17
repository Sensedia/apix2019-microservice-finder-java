package com.sensedia.apix2019.microservice.finder;

import com.sensedia.apix2019.microservice.finder.verticles.RabbitMQConsumerVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class VertxApp {

    public static void main(String [] args){
        Vertx vertx = Vertx.vertx();

        setAppConfiguration(vertx).getConfig( ar -> {
            if(ar.succeeded()) {
                vertx.deployVerticle(new RabbitMQConsumerVerticle(), new DeploymentOptions().setConfig(ar.result()));
            } else {
                System.out.println("Error retrieving config file -> " + ar.cause().getMessage());
            }
        });
    }

    private static ConfigRetriever setAppConfiguration(Vertx vertx){
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "src/main/resources/application-conf.json"));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        return retriever;
    }
}
