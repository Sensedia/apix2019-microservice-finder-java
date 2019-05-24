package com.sensedia.apix2019.microservice.finder.configuration;

import io.vertx.core.json.JsonObject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import static com.sensedia.apix2019.microservice.finder.commons.ConfigConstants.ES_HOSTNAME;
import static com.sensedia.apix2019.microservice.finder.commons.ConfigConstants.ES_PORT;
import static com.sensedia.apix2019.microservice.finder.commons.ConfigConstants.ES_SCHEME;

public final class ElasticSearchConfiguration {

    private ElasticSearchConfiguration() {
    }

    public static RestHighLevelClient createElasticSearchClient(final JsonObject elasticSearchConfig) {

        final String hostName = elasticSearchConfig.getString(ES_HOSTNAME);
        final String scheme = elasticSearchConfig.getString(ES_SCHEME);
        final Integer port = elasticSearchConfig.getInteger(ES_PORT);

        return new RestHighLevelClient(RestClient.builder(new HttpHost(hostName, port, scheme)));
    }
}
