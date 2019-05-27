package com.sensedia.apix2019.microservice.finder.verticle;

import com.sensedia.apix2019.microservice.finder.commons.ConfigConstants;
import com.sensedia.apix2019.microservice.finder.configuration.RabbitMQConfiguration;
import com.sensedia.apix2019.microservice.finder.enumeration.FinderEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rabbitmq.RabbitMQClient;

import java.util.Objects;

public class RabbitMQVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQVerticle.class);

    private static final String BODY = "body";
    private static final String PHONE = "phone";

    private JsonObject config;
    private RabbitMQClient client;
    private String queueSpecificationName;
    private String queueRecommendationName;
    private String queueNotificationName;

    @Override
    public void init(Vertx vertx, Context ctx) {

        super.init(vertx, ctx);

        config = ctx.config().getJsonObject(ConfigConstants.RABBITMQ_CONFIG_KEY);
        client = RabbitMQConfiguration.createRabbitMQClient(vertx, config);

        queueSpecificationName = config.getString(ConfigConstants.RABBITMQ_QUEUE_SPECIFICATION_NAME_ATTR);
        queueRecommendationName = config.getString(ConfigConstants.RABBITMQ_QUEUE_RECOMMENDATION_NAME_ATTR);
        queueNotificationName = config.getString(ConfigConstants.RABBITMQ_QUEUE_NOTIFICATION_NAME_ATTR);
    }

    @Override
    public void start() {

        client.start(result -> {
            if (result.succeeded()) {
                logger.info("Rabbit verticle connected. Waiting for messages");
                getMessage();
                consumeElasticSearchQueryDone();

            } else {
                logger.error("Error in worker connection");
            }
        });
    }

    private void getMessage() {

        final EventBus eventBus = vertx.eventBus();

        vertx.setPeriodic(1000, handler -> {

            client.basicGet(queueSpecificationName, true, getResult -> {

                if (getResult.succeeded()) {
                    JsonObject msg = getResult.result();

                    if (Objects.nonNull(msg)) {
                        String msgPayload = msg.getString(BODY);

                        logger.info("Received message: {}", msgPayload);

                        eventBus.send(FinderEvent.ES_SEARCH_EVENT.name(), msgPayload);
                    }

                } else {
                    logger.error("Error during connection. Cause {}", getResult.cause());
                    logger.error("Trying to recreate queue {}", queueSpecificationName);
                    createQueue();
                }
            });
        });
    }

    private void consumeElasticSearchQueryDone() {
        vertx.eventBus()
                .consumer(FinderEvent.ES_QUERY_DONE_EVENT.name(), this::publishKitResponse);
    }

    private void publishKitResponse(Message<String> message) {
        JsonObject notificationPayload = new JsonObject().put("body", message.headers().get(PHONE));
        JsonObject recommendationsPayload = new JsonObject().put("body", message.body());

        client.basicPublish("", queueNotificationName, notificationPayload, this.publishHandler(queueNotificationName));
        client.basicPublish("", queueRecommendationName, recommendationsPayload, this.publishHandler(queueRecommendationName));
    }

    private Handler<AsyncResult<Void>> publishHandler(String queueName) {
        return (AsyncResult<Void> pubResult) -> {
            if (pubResult.succeeded()) {
                logger.info("Message on '{}' queue published!", queueName);
            } else {
                logger.error("Error during message publish on queue '{}'. Cause {}", queueName, pubResult.cause());
            }
        };
    }

    private void createQueue() {
        client.queueDeclare(queueSpecificationName, true, false, true, queueResult -> {
            if (queueResult.succeeded()) {
                logger.info("Queue {} created!", queueSpecificationName);
                logger.info("Waiting for messages...");

            } else {
                logger.error("Error creating queue {}. Cause: {}", queueSpecificationName, queueResult.cause());
            }
        });
    }
}