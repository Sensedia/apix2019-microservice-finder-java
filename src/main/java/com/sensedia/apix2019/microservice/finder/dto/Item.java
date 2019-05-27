package com.sensedia.apix2019.microservice.finder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sensedia.apix2019.microservice.finder.enumeration.Type;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Serializable {

    private Type type;
    private String title;
    private Double price;
    private String color;
    private String link;
    private String image;
    private String date;

    public Item() {
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Double getPrice() {
        return price;
    }

    public String getColor() {
        return color;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }
}
