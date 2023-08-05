package com.example.restful.Json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.List;

public class JsonConverter {
    private static ObjectMapper objectMapper;
    private static ObjectWriter ow;

    private JsonConverter() {};

    static {
        objectMapper = new ObjectMapper();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static <T> T getObject(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, new CustomTypeReference<T>(type));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> List<T> getObjects(String json, Class<T> type) {
        try {

            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, type));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> String getJson(T t) {
        try {
            return ow.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
