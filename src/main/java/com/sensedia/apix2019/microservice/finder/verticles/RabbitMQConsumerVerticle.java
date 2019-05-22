package com.sensedia.apix2019.microservice.finder.verticles;

import com.sensedia.apix2019.microservice.finder.commons.Constants;
import com.sensedia.apix2019.microservice.finder.configuration.RabbitMQConfiguration;
import com.sensedia.apix2019.microservice.finder.enumeration.FinderEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rabbitmq.RabbitMQClient;

import java.util.Objects;

public class RabbitMQConsumerVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumerVerticle.class);

    private static final String BODY = "body";

    private JsonObject config;
    private RabbitMQClient client;
    private String queueName;

    @Override
    public void init(Vertx vertx, Context ctx) {

        super.init(vertx, ctx);

        config = ctx.config().getJsonObject(Constants.RABBITMQ_CONFIG_KEY);
        client = RabbitMQConfiguration.createRabbitMQInstance(vertx, config);

        queueName = config.getString(Constants.RABBITMQ_QUEUE_NAME_ATTR);
    }

    @Override
    public void start() {

        client.start(result -> {
            if (result.succeeded()) {
                logger.info("Worker connected - Waiting for messages");
                getMessage();

            } else {
                logger.error("Error in worker connection");
            }
        });

    }

    private void getMessage() {

        final EventBus eventBus = vertx.eventBus();

        vertx.setPeriodic(1000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {

                client.basicGet(queueName, true, getResult -> {

                    if (getResult.succeeded()) {
                        JsonObject msg = getResult.result();

                        if (Objects.nonNull(msg)) {
                            String msgPayload = msg.getString(BODY);
                            logger.info("Received message: {}", msgPayload);

                            eventBus.send(FinderEvent.ES_QUERY_EVENT.name(), msgPayload);
                        }

                    } else {
                        logger.error("Error during connection. Cause {}", getResult.cause());
                        logger.error("Trying to recreate queue {}", queueName);
                        createQueue();
                    }
                });
            }
        });
    }

    private void createQueue() {

        client.queueDeclare(queueName, true, false, true, queueResult -> {
            if (queueResult.succeeded()) {
                logger.info("Queue {} created!", queueName);
                logger.info("Waiting for messages...");

            } else {
                logger.error("Error creating queue {}. Cause: {}", queueName, queueResult.cause());
            }
        });
    }

}
