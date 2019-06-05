package com.sensedia.apix2019.microservice.finder.verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.apix2019.microservice.finder.dto.KitRequest;
import com.sensedia.apix2019.microservice.finder.dto.KitResponse;
import com.sensedia.apix2019.microservice.finder.enumeration.FinderEvent;
import com.sensedia.apix2019.microservice.finder.utils.ElasticSearchKitRequestBuilder;
import com.sensedia.apix2019.microservice.finder.utils.ElasticSearchKitResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

import static com.sensedia.apix2019.microservice.finder.commons.ConfigConstants.ELASTIC_SEARCH;
import static com.sensedia.apix2019.microservice.finder.configuration.ElasticSearchConfiguration.createElasticSearchClient;


public class ElasticSearchVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchVerticle.class);

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

            client.msearchAsync(ElasticSearchKitRequestBuilder.build(kitRequest), RequestOptions.DEFAULT,
                    new ActionListener<MultiSearchResponse>() {

                        @Override
                        public void onResponse(final MultiSearchResponse result) {
                            handleKitsSearchResponse(kitRequest.getId(), result);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            logger.error("Elastic search call has failed.", e);
                        }
                    });

        } catch (IOException e) {
            logger.error("Message Conversion failed. Payload {}.", message, e);
        }
    }

    private void handleKitsSearchResponse(final String kitId, final MultiSearchResponse result) {

        try {
            KitResponse kitResponse = ElasticSearchKitResponseBuilder.build(kitId, result);
            logger.info("Number of kits found: {}", kitResponse.getRecommendations().size());

            String kitResponseJson = objectMapper.writeValueAsString(kitResponse);
            logger.info("Kits: {}", kitResponseJson);

            vertx.eventBus().send(FinderEvent.ES_QUERY_DONE_EVENT.name(), kitResponseJson);

        } catch (JsonProcessingException e) {
            logger.error("Error sending message by event bus.", e);
        }
    }

    @Override
    public void stop() throws Exception {

        client.close();
    }
}
