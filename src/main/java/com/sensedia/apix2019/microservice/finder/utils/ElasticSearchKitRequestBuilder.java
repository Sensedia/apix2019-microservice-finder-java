package com.sensedia.apix2019.microservice.finder.utils;

import com.sensedia.apix2019.microservice.finder.dto.KitRequest;
import com.sensedia.apix2019.microservice.finder.dto.Specification;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;

import static com.sensedia.apix2019.microservice.finder.commons.ConfigConstants.RECOMMENDATIONS_NUMBER_LIMIT;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.COLOR;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.GENDER;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.PRICE;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.SUGGESTION;
import static com.sensedia.apix2019.microservice.finder.commons.SearchConstants.TYPE;
import static org.elasticsearch.search.sort.SortOrder.ASC;

public class ElasticSearchKitRequestBuilder {

    private ElasticSearchKitRequestBuilder() {
    }

    public static MultiSearchRequest build(final KitRequest kitRequest) {

        final String gender = kitRequest.getGender().name();
        final MultiSearchRequest multiSearchRequest = new MultiSearchRequest();

        kitRequest.getSpecifications().forEach(specification -> multiSearchRequest.add(buildKitsSearch(specification, gender)));

        return multiSearchRequest;
    }

    private static SearchRequest buildKitsSearch(final Specification specification, final String gender) {

        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery(COLOR, specification.getColor().name()))
                .must(QueryBuilders.termQuery(TYPE, specification.getType().name()))
                .must(QueryBuilders.termQuery(GENDER, gender));

        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(queryBuilder)
                .fetchSource(true)
                .sort(new FieldSortBuilder(PRICE).order(ASC))
                .size(RECOMMENDATIONS_NUMBER_LIMIT);

        return new SearchRequest(SUGGESTION).source(searchSourceBuilder);
    }
}
