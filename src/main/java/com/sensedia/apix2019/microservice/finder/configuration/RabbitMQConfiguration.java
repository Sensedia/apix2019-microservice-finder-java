package com.sensedia.apix2019.microservice.finder.configuration;

import com.sensedia.apix2019.microservice.finder.commons.ConfigConstants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

public final class RabbitMQConfiguration {

    private RabbitMQConfiguration() {
    }

    public static RabbitMQClient createRabbitMQClient(Vertx vertx, JsonObject config) {
        RabbitMQOptions rabbitOpts = new RabbitMQOptions();
        rabbitOpts.setUri(config.getString(ConfigConstants.RABBITMQ_CONN_URL_ATTR));
        return RabbitMQClient.create(vertx, rabbitOpts);
    }

}
