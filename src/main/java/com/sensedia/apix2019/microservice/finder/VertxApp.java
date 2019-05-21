package com.sensedia.apix2019.microservice.finder;

import com.sensedia.apix2019.microservice.finder.verticles.RabbitMQConsumerVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;

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
                .setConfig(new JsonObject().put("path", setConfigurationFilePath()));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        return retriever;
    }

    private static String setConfigurationFilePath(){
        String finderConfigFile = System.getenv("FINDER_CONFIG_FILE");
        if(StringUtils.isBlank(finderConfigFile)){
            finderConfigFile = "src/main/resources/application-conf.json";
            System.out.println("[*] Environment Variable FINDER_CONFIG_FILE not set!");
            System.out.println("[*] Starting application with default configuration file -> " + finderConfigFile);
        } else {
            System.out.println("[*] Environment Variable FINDER_CONFIG_FILE value found!");
            System.out.println("[*] Starting application using file -> " + finderConfigFile);
        }

        return finderConfigFile;
    }

}
