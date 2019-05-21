package com.sensedia.apix2019.microservice.finder.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.apix2019.microservice.finder.dto.IncomeMessage;

import java.io.IOException;

public class MessageUtils {

    public static IncomeMessage mapToObject(String content) {
        try {
            System.out.println("[*] Converting received message to Java Object...");
            ObjectMapper mapper = new ObjectMapper();
            com.sensedia.apix2019.microservice.finder.dto.IncomeMessage incMsg = mapper.readValue(content, IncomeMessage.class);
            System.out.println("[*] Message converted!");
            return incMsg;
        } catch(IOException e){
            System.out.println("[x] Fail in message converting..." + e.getMessage());
        }
        return null;
    }
}
