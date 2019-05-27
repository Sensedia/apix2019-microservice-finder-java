package com.sensedia.apix2019.microservice.finder.dto;

import com.sensedia.apix2019.microservice.finder.enumeration.Gender;

import java.io.Serializable;
import java.util.List;

public class KitRequest implements Serializable {

    private Long id;
    private String phone;
    private Gender gender;
    private List<Specification> specifications;

    public KitRequest() { }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }

    public Long getId() { return id; }

    public String getPhone() {
        return phone;
    }


    public Gender getGender() {
        return gender;
    }

    public List<Specification> getSpecifications() {
        return specifications;
    }

}
