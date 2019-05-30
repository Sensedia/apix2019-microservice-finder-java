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
    private static final String NUMBER_OF_COMBINATIONS_FOUND = "numberOfCombinationsFound";

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
                    logger.error("Error during connection. Cause {}", getResult.cause().getCause().getMessage());
                    logger.error("Trying to recreate queue {}", queueSpecificationName);
                }
            });
        });
    }

    private void consumeElasticSearchQueryDone() {

        vertx.eventBus().consumer(FinderEvent.ES_QUERY_DONE_EVENT.name(), this::publishKitResponse);
    }

    private void publishKitResponse(final Message<String> message) {

        publishToKitsService(message);
        publishMsgToNotificationService(message);
    }

    private void publishMsgToNotificationService(final Message<String> message) {

        JsonObject notificationJsonObj = new JsonObject().put(PHONE, message.headers().get(PHONE))
                .put(NUMBER_OF_COMBINATIONS_FOUND, message.headers().get(NUMBER_OF_COMBINATIONS_FOUND));

        JsonObject notificationPayload = new JsonObject().put(BODY, notificationJsonObj.toString());
        client.basicPublish("", queueNotificationName, notificationPayload, publishHandler(queueNotificationName));
    }

    private void publishToKitsService(final Message<String> message) {

        JsonObject recommendationsPayload = new JsonObject().put(BODY, message.body());
        client.basicPublish("", queueRecommendationName, recommendationsPayload, publishHandler(queueRecommendationName));
    }

    private Handler<AsyncResult<Void>> publishHandler(final String queueName) {

        return (AsyncResult<Void> pubResult) -> {
            if (pubResult.succeeded()) {
                logger.info("Message on '{}' queue published!", queueName);
            } else {
                logger.error("Error during message publish on queue '{}'. Cause {}", queueName, pubResult.cause());
            }
        };
    }
}
