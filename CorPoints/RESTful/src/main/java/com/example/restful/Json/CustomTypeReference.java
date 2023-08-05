package com.example.restful.Json;

import com.fasterxml.jackson.core.type.TypeReference;

class CustomTypeReference<T> extends TypeReference<T> {
    private final Class<T> type;

    public CustomTypeReference(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}