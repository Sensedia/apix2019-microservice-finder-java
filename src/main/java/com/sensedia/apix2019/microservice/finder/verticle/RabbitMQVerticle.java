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
import io.vertx.rabbitmq.RabbitMQConsumer;

public class RabbitMQVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQVerticle.class);

    private static final String BODY = "body";

    private JsonObject config;
    private RabbitMQClient client;
    private String queueSpecificationName;
    private String queueRecommendationName;

    @Override
    public void init(Vertx vertx, Context ctx) {

        super.init(vertx, ctx);

        config = ctx.config().getJsonObject(ConfigConstants.RABBITMQ_CONFIG_KEY);
        client = RabbitMQConfiguration.createRabbitMQClient(vertx, config);

        queueSpecificationName = config.getString(ConfigConstants.RABBITMQ_QUEUE_SPECIFICATION_NAME_ATTR);
        queueRecommendationName = config.getString(ConfigConstants.RABBITMQ_QUEUE_RECOMMENDATION_NAME_ATTR);
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

        client.basicConsumer(queueSpecificationName, rabbitConsumerResultAsync -> {

            RabbitMQConsumer consumerResult = rabbitConsumerResultAsync.result();

            if (rabbitConsumerResultAsync.succeeded()) {
                consumerResult.handler(message -> {
                    String msgPayload = message.body().toString();
                    logger.info("Received message {} .", msgPayload);

                    eventBus.send(FinderEvent.ES_SEARCH_EVENT.name(), msgPayload);
                });
            } else {
                logger.error("Error consuming message {}. ", rabbitConsumerResultAsync.cause());
            }
        });
    }

    private void consumeElasticSearchQueryDone() {

        vertx.eventBus().consumer(FinderEvent.ES_QUERY_DONE_EVENT.name(), this::publishKitResponse);
    }

    private void publishKitResponse(final Message<String> message) {

        publishToKitsService(message);
    }

    private void publishToKitsService(final Message<String> message) {

        JsonObject recommendationsPayload = new JsonObject().put(BODY, message.body());
        client.basicPublish("", queueRecommendationName, recommendationsPayload, publishHandler(queueRecommendationName));
    }

    private Handler<AsyncResult<Void>> publishHandler(final String queueName) {

        return (AsyncResult<Void> pubResult) -> {
            if (pubResult.succeeded()) {
                logger.info("Message published to queue '{}' published!", queueName);
            } else {
                logger.error("Error during message publish on queue '{}'. Cause {}", queueName, pubResult.cause());
            }
        };
    }

    @Override
    public void stop() {

        client.stop(stopHandler -> {
            if (stopHandler.succeeded()) {
                logger.info("Rabbit client was stopped!");
            } else {
                logger.error("Something wrong stopping Rabbitmq client {}", stopHandler.cause());
            }
        });
    }
}
