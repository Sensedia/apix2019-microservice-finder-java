package com.sensedia.apix2019.microservice.finder.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.apix2019.microservice.finder.dto.Item;
import com.sensedia.apix2019.microservice.finder.dto.KitResponse;
import com.sensedia.apix2019.microservice.finder.enumeration.Gender;
import com.sensedia.apix2019.microservice.finder.enumeration.Type;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.search.MultiSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class ElasticSearchKitResponseBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchKitResponseBuilder.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static KitResponse build(final Long id, final Gender gender, final MultiSearchResponse result) {

        Map<Type, List<Item>> items = extractItemsByType(result);
        return build(id, gender, items);
    }

    private static Map<Type, List<Item>> extractItemsByType(final MultiSearchResponse result) {

        final Map<Type, List<Item>> itemsByType = new HashMap<>();

        for (Type type : Type.values()) {
            itemsByType.put(type, new ArrayList<>());
        }

        for (int i = 0; i < result.getResponses().length; i++) {

            Optional<Pair<Type, List<Item>>> itemsByTypePair = extractItems(result.getResponses()[i]);
            if (itemsByTypePair.isPresent()) {

                Pair<Type, List<Item>> pair = itemsByTypePair.get();
                itemsByType.put(pair.getKey(), pair.getValue());
            }
        }

        return itemsByType;
    }

    private static Optional<Pair<Type, List<Item>>> extractItems(final MultiSearchResponse.Item result) {

        final List<Item> items = new ArrayList<>();

        for (int i = 0; i < result.getResponse().getHits().getHits().length; i++) {

            try {
                String sourceAsString = result.getResponse().getHits().getHits()[i].getSourceAsString();
                items.add(objectMapper.readValue(sourceAsString, Item.class));

            } catch (IOException e) {
                logger.error("Error converting elastic search source response. ", e);
            }
        }

        if (!items.isEmpty()) {
            return Optional.of(new ImmutablePair<>(items.get(1).getType(), items));
        }

        return Optional.empty();
    }

    private static KitResponse build(final Long id, final Gender gender, final Map<Type, List<Item>> itemsByType) {

        final IntStream kitsRange = IntStream.range(0, retrieveNumberOfKitsFound(itemsByType));

        final List<List<Item>> recommendations = kitsRange.mapToObj((index) -> getIndexItemsFromTypes(index, itemsByType))
                .collect(toList());

        return KitResponse.builder()
                .id(id)
                .gender(gender)
                .recommendations(recommendations)
                .build();
    }

    private static int retrieveNumberOfKitsFound(final Map<Type, List<Item>> itemsByType) {

        List<Integer> numberOfItemsFoundPerType = new ArrayList<>();
        itemsByType.forEach((k, v) -> numberOfItemsFoundPerType.add(v.size()));
        return numberOfItemsFoundPerType.stream().mapToInt(v -> v).min().orElse(0);
    }

    private static List<Item> getIndexItemsFromTypes(final int index, final Map<Type, List<Item>> itemsByType) {

        return itemsByType.entrySet()
                .stream()
                .map((entry) -> entry.getValue().get(index))
                .collect(toList());
    }
}
