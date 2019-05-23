package com.sensedia.apix2019.microservice.finder.verticles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubrick.vertx.elasticsearch.ElasticSearchService;
import com.hubrick.vertx.elasticsearch.model.SearchOptions;
import com.hubrick.vertx.elasticsearch.model.SortOrder;
import com.sensedia.apix2019.microservice.finder.dto.KitRequest;
import com.sensedia.apix2019.microservice.finder.enumeration.FinderEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;

public class ElasticSearchVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchVerticle.class);

    private static final String EVENT_BUS_ADDRESS = "es-eventbus-address";

    private ElasticSearchService elasticSearchService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(Vertx vertx, Context ctx) {
        super.init(vertx, ctx);
    }

    @Override
    public void start() {

        final EventBus eventBus = vertx.eventBus();
        eventBus.localConsumer(EVENT_BUS_ADDRESS).handler(result -> result.reply(result.body()));
        eventBus.consumer(FinderEvent.ES_SEARCH_EVENT.name(), this::searchHandler);

        elasticSearchService = ElasticSearchService.createEventBusProxy(vertx, EVENT_BUS_ADDRESS);
    }

    private void searchHandler(Message<Object> message) {

        try {
            String msgPayload = (String) message.body();
            KitRequest kit = objectMapper.readValue(msgPayload, KitRequest.class);

            final SearchOptions searchOptions = new SearchOptions()
                    .setQuery(new JsonObject("{\"match_all\": {}}"))
                    .setFetchSource(true)
                    .addFieldSort("price", SortOrder.DESC);


//            elasticSearchService.get("twitter", "tweet", "123", getOptions, getResponse -> {
//
//                if (getResponse.succeeded()) {
//                    logger.info("ES result " + getResponse.result().toJson());
//                } else {
//                    logger.error("Ble {}", getResponse.cause());
//                }
//            });

        } catch (IOException e) {
            logger.error("Message Conversion failed. Payload {}", message, e);
        }
    }
}
