package com.sensedia.apix2019.microservice.finder.dto;

import com.sensedia.apix2019.microservice.finder.dto.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeMessage {

    private Long id;
    private String phone;
    private GenderEnum gender;
    private List<Specification> specifications;

}
