package com.sensedia.apix2019.microservice.finder.configuration;

import com.sensedia.apix2019.microservice.finder.commons.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

public class RabbitMQConfiguration {

    public static RabbitMQClient createRabbitMQInstance(Vertx vertx, JsonObject config){
        RabbitMQOptions rabbitOpts = new RabbitMQOptions();
        rabbitOpts.setUri(config.getString(Constants.RABBITMQ_CONN_URL_ATTR));
        return RabbitMQClient.create(vertx, rabbitOpts);
    }

}
