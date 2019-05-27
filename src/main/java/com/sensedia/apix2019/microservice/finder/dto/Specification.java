package com.sensedia.apix2019.microservice.finder.dto;

import com.sensedia.apix2019.microservice.finder.enumeration.Color;
import com.sensedia.apix2019.microservice.finder.enumeration.Type;

import java.io.Serializable;

public class Specification implements Serializable {

    private Type type;
    private Color color;

    public Specification() {
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }
}
