package com.sensedia.apix2019.microservice.finder.dto;

import com.sensedia.apix2019.microservice.finder.enumeration.Gender;

import java.io.Serializable;
import java.util.List;

public class KitResponse implements Serializable {

    private Long id;
    private Gender gender;
    private List<List<Item>> recommendations;

    public KitResponse() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setRecommendations(List<List<Item>> recommendations) {
        this.recommendations = recommendations;
    }

    public List<List<Item>> getRecommendations() {
        return recommendations;
    }

    public Long getId() {
        return id;
    }

    public Gender getGender() {
        return gender;
    }
}
