package com.yourcompany.flink.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {
    public static final ObjectMapper mapper = new ObjectMapper();

    public JsonMapper() {
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
}
