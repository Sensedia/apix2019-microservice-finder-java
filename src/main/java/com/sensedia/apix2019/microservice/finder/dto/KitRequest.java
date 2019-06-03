package com.sensedia.apix2019.microservice.finder.dto;

import com.sensedia.apix2019.microservice.finder.enumeration.Gender;

import java.io.Serializable;
import java.util.List;

public class KitRequest implements Serializable {

    private String id;
    private Gender gender;
    private List<Specification> specifications;

    public KitRequest() { }

    public void setId(String id) {
        this.id = id;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }

    public String getId() { return id; }

    public Gender getGender() {
        return gender;
    }

    public List<Specification> getSpecifications() {
        return specifications;
    }

}
