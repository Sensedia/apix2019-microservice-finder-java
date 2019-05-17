package com.sensedia.apix2019.microservice.finder.verticles;

import com.sensedia.apix2019.microservice.finder.commons.Constants;
import com.sensedia.apix2019.microservice.finder.configuration.RabbitMQConfiguration;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.util.Objects;

public class RabbitMQConsumerVerticle extends AbstractVerticle {

    protected JsonObject config;
    protected RabbitMQClient client;

    private String queueName;

    @Override
    public void init(Vertx vertx, Context ctx){
        super.init(vertx, ctx);

        config = ctx.config().getJsonObject(Constants.RABBITMQ_CONFIG_KEY);
        client = RabbitMQConfiguration.createRabbitMQInstance(vertx, config);

        queueName = config.getString(Constants.RABBITMQ_QUEUE_NAME_ATTR);
    }

    @Override
    public void start(){

        client.start(result -> {
            if(result.succeeded()){
                System.out.println("[*] Worker connected");
                getMessage();
            } else {
                System.out.println("[x] Error in worker connection");
            }
        });

    }

    private void getMessage(){
        vertx.setPeriodic(1000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {
                client.basicGet(queueName, true, getResult -> {
                    if (getResult.succeeded()) {
                        JsonObject msg = getResult.result();
                        if(Objects.nonNull(msg)) {
                            System.out.println("[*] Received message: " + msg.getString("body"));
                        }
                    } else {
                        System.out.println("[x] Error during connection: " + getResult.cause());
                        getResult.cause().printStackTrace();
                        System.out.println("[*] Trying to recreate queue ->" + queueName);
                        declareQueue();
                    }
                });
            }
        });

    }

    private void declareQueue(){
        client.queueDeclare(queueName, true, false, true, queueResult -> {
            if(queueResult.succeeded()){
                System.out.println("[*] Queue " + queueName + " created!");
                System.out.println("[*] Waiting for messages...");
            } else {
                System.err.println("[x] Error creating queue ->" + queueName);
                queueResult.cause().printStackTrace();
            }
        });
    }
}
