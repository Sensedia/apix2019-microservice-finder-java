package com.sensedia.apix2019.microservice.finder.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.Objects;

public class RabbitMQConsumerVerticle extends AbstractVerticle {

    protected RabbitMQClient client;

    private static final String QUEUE_NAME = "apix2019-specification-queue";
    @Override
    public void init(Vertx vertx, Context ctx){
        super.init(vertx, ctx);
        RabbitMQOptions config = new RabbitMQOptions();
        // full amqp uri
        config.setUri("amqp://localhost:5672");
        client = RabbitMQClient.create(vertx, config);
    }

    @Override
    public void start(){

        client.start(result -> {
            if(result.succeeded()){
                System.out.println("[*] Worker connected - Waiting for messages");
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
                client.basicGet(QUEUE_NAME, true, getResult -> {
                    if (getResult.succeeded()) {
                        JsonObject msg = getResult.result();
                        if(Objects.nonNull(msg)) {
                            System.out.println("[*] Received message: " + msg.getString("body"));
                        }
                    } else {
                        System.out.println("[x] Error during connection: " + getResult.cause());
                        getResult.cause().printStackTrace();
                        System.out.println("[*] Trying to recreate queue ->" + QUEUE_NAME);
                        declareQueue();
                    }
                });
            }
        });

    }

    private void declareQueue(){
        client.queueDeclare(QUEUE_NAME, true, false, true, queueResult -> {
            if(queueResult.succeeded()){
                System.out.println("[*] Queue " + QUEUE_NAME + " created!");
                System.out.println("[*] Waiting for messages...");
            } else {
                System.err.println("[x] Error creating queue " + QUEUE_NAME);
                queueResult.cause().printStackTrace();
            }
        });
    }
}
