package com.sensedia.apix2019.microservice.finder;

import com.sensedia.apix2019.microservice.finder.verticle.ElasticSearchVerticle;
import com.sensedia.apix2019.microservice.finder.verticle.RabbitMQVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class VertxApp {

    private static final Logger logger = LoggerFactory.getLogger(VertxApp.class);

    private static final String FINDER_CONFIG_FILE = "FINDER_CONFIG_FILE";
    private static final String DEFAULT_CONFIG_FILE = "src/main/resources/application-conf.json";

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        setAppConfiguration(vertx).getConfig(ar -> {
            if (ar.succeeded()) {
                vertx.deployVerticle(new RabbitMQVerticle(), new DeploymentOptions().setConfig(ar.result()));
                vertx.deployVerticle(new ElasticSearchVerticle(), new DeploymentOptions().setConfig(ar.result()));
            } else {
                logger.error("Error retrieving config file -> ", ar.cause().getMessage());
            }
        });
    }

    private static ConfigRetriever setAppConfiguration(Vertx vertx) {

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", setConfigurationFilePath()));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);

        return ConfigRetriever.create(vertx, options);
    }

    private static String setConfigurationFilePath() {

        String finderConfigFile = System.getenv(FINDER_CONFIG_FILE);

        if (isBlank(finderConfigFile)) {
            logger.warn("Environment Variable FINDER_CONFIG_FILE not set!");
            logger.warn("Starting application with default configuration file -> {}", DEFAULT_CONFIG_FILE);
            return DEFAULT_CONFIG_FILE;

        } else {
            logger.info("Environment Variable FINDER_CONFIG_FILE value found!");
            logger.info("Starting application using file -> {}", finderConfigFile);
        }

        return finderConfigFile;
    }

}
