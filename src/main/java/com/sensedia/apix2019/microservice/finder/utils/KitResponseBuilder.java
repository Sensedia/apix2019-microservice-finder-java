package com.sensedia.apix2019.microservice.finder.utils;

import com.sensedia.apix2019.microservice.finder.dto.Item;
import com.sensedia.apix2019.microservice.finder.dto.KitResponse;
import com.sensedia.apix2019.microservice.finder.enumeration.Gender;
import com.sensedia.apix2019.microservice.finder.enumeration.Type;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sensedia.apix2019.microservice.finder.commons.Constants.RECOMENDATION_NUMBER_OF_KITS;

public class KitResponseBuilder {

    public KitResponse build(Long id, Gender gender, List<Item> items){
        final Map<Type, List<Item>> itemsByType = items.stream().collect(Collectors.groupingBy(Item::getType));

        final IntStream kitsRange = IntStream.range(0, RECOMENDATION_NUMBER_OF_KITS);

        final List<List<Item>> recomendations = kitsRange.mapToObj((index) -> this.getIndexItemsFromTypes(index, itemsByType))
                                                         .collect(Collectors.toList());

        return KitResponse.builder()
                          .id(id)
                          .gender(gender)
                          .recomendations(recomendations)
                          .build();
    }

    private List<Item> getIndexItemsFromTypes(int index, Map<Type, List<Item>> itemsByType) {
        return itemsByType.entrySet()
                          .stream()
                          .map((entry) -> entry.getValue().get(index))
                          .collect(Collectors.toList());
    }
}
