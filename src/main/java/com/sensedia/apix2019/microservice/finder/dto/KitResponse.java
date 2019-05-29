package com.sensedia.apix2019.microservice.finder.dto;

import java.io.Serializable;
import java.util.List;

public class KitResponse implements Serializable {

    private String id;
    private List<List<Item>> recommendations;

    public KitResponse() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRecommendations(List<List<Item>> recommendations) {
        this.recommendations = recommendations;
    }

    public List<List<Item>> getRecommendations() {
        return recommendations;
    }

    public String getId() {
        return id;
    }
}
