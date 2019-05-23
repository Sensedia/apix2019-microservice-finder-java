package com.sensedia.apix2019.microservice.finder.dto;

import com.sensedia.apix2019.microservice.finder.enumeration.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Type type;
    private String title;
    private Double price;
    private String color;
    private String link;
    private String image;
    private String date;

}
