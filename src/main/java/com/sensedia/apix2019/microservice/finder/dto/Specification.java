package com.sensedia.apix2019.microservice.finder.dto;

import com.sensedia.apix2019.microservice.finder.dto.enums.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specification {

    private TypeEnum type;
    private String color;

}
