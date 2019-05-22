package com.sensedia.apix2019.microservice.finder.verticles;

import com.sensedia.apix2019.microservice.finder.dto.IncomeMessage;
import com.sensedia.apix2019.microservice.finder.enumeration.FinderEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ElasticSearchVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchVerticle.class);

    @Override
    public void init(Vertx vertx, Context ctx) {
        super.init(vertx, ctx);
    }

    @Override
    public void start() {

        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(FinderEvent.ES_QUERY_EVENT.name(), message -> {
            JsonObject json = ((JsonObject) message.body());
            IncomeMessage incomeMessage = Json.mapper.convertValue(json.getMap(), IncomeMessage.class);

            logger.info("Andreia = " + incomeMessage.getPhone());
        });
    }
}
