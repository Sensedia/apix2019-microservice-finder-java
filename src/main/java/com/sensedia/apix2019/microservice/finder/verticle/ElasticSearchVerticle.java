package com.sensedia.apix2019.microservice.finder.verticle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.apix2019.microservice.finder.dto.KitRequest;
import com.sensedia.apix2019.microservice.finder.enumeration.FinderEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

import static com.sensedia.apix2019.microservice.finder.commons.ConfigConstants.ELASTIC_SEARCH;
import static com.sensedia.apix2019.microservice.finder.configuration.ElasticSearchConfiguration.createElasticSearchClient;


public class ElasticSearchVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchVerticle.class);

    private static final String PHONE = "phone";
    private static final String NUMBER_OF_COMBINATIONS_FOUND = "numberOfCombinationsFound";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestHighLevelClient client;

    @Override
    public void init(final Vertx vertx, final Context ctx) {

        super.init(vertx, ctx);
        client = createElasticSearchClient(config().getJsonObject(ELASTIC_SEARCH));
    }

    @Override
    public void start() {

        vertx.eventBus().consumer(FinderEvent.ES_SEARCH_EVENT.name(), this::searchKits);
    }

    private void searchKits(final Message<String> message) {

        try {
            KitRequest kitRequest = objectMapper.readValue(message.body(), KitRequest.class);

            // TODO: Cadê a busquinha d@ mamãe / papai?

        } catch (IOException e) {
            logger.error("Message Conversion failed. Payload {}.", message, e);
        }
    }

}
