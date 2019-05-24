package com.sensedia.apix2019.microservice.finder.verticles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.apix2019.microservice.finder.dto.KitRequest;
import com.sensedia.apix2019.microservice.finder.dto.Specification;
import com.sensedia.apix2019.microservice.finder.enumeration.FinderEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;

import static com.sensedia.apix2019.microservice.finder.commons.ConfigConstants.ELASTIC_SEARCH;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.COLOR;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.GENDER;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.PRICE;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.RECOMMENDATION;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.TYPE;
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

        vertx.eventBus().consumer(FinderEvent.ES_SEARCH_EVENT.name(), this::search);
    }

    private void search(final Message<Object> message) {

        try {
            KitRequest kitRequest = objectMapper.readValue((String) message.body(), KitRequest.class);

            client.msearchAsync(buildMultiSearchRequest(kitRequest), RequestOptions.DEFAULT,
                    new ActionListener<MultiSearchResponse>() {

                        @Override
                        public void onResponse(MultiSearchResponse result) {
                            logger.info("Resultado 1 : {}", result.getResponses()[0].getResponse());
                            logger.info("Resultado 2 : {}", result.getResponses()[1].getResponse());
                            logger.info("Resultado 3 : {}", result.getResponses()[2].getResponse());
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

    private MultiSearchRequest buildMultiSearchRequest(final KitRequest kitRequest) {

        final String gender = kitRequest.getGender().name();
        final MultiSearchRequest multiSearchRequest = new MultiSearchRequest();

        kitRequest.getSpecifications().forEach(specification -> multiSearchRequest.add(buildSpecificationSearch(specification, gender)));

        return multiSearchRequest;
    }

    private SearchRequest buildSpecificationSearch(final Specification specification, final String gender) {

        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.fuzzyQuery(COLOR, specification.getColor().name()))
                .must(QueryBuilders.fuzzyQuery(TYPE, specification.getType().name()))
                .must(QueryBuilders.fuzzyQuery(GENDER, gender));

        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(queryBuilder)
                .fetchSource(true)
                .sort(new FieldSortBuilder(PRICE).order(SortOrder.ASC))
                .size(3);

        return new SearchRequest(RECOMMENDATION).source(searchSourceBuilder);
    }
}
