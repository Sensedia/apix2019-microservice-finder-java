package com.sensedia.apix2019.microservice.finder.commons;

public final class ConfigConstants {

    private ConfigConstants() {
    }

    // Rabbit configurations
    public static final String RABBITMQ_CONFIG_KEY = "rabbit";
    public static final String RABBITMQ_CONN_URL_ATTR = "connectionUrl";
    public static final String RABBITMQ_QUEUE_SPECIFICATION_NAME_ATTR = "queueSpecificationName";
    public static final String RABBITMQ_QUEUE_RECOMMENDATION_NAME_ATTR = "queueRecomendationName";
    public static final String RABBITMQ_QUEUE_NOTIFICATION_NAME_ATTR = "queueNotificationName";

    // Elastic search configurations
    public static final String ELASTIC_SEARCH = "elastic_search";
    public static final String ES_HOSTNAME = "hostname";
    public static final String ES_PORT = "port";
    public static final String ES_SCHEME = "scheme";

    // General configurations
    public static final Integer RECOMMENDATIONS_NUMBER_LIMIT = 3;
}
