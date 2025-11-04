package com.bizmate.groupware.approval.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class JsonMapConverter implements AttributeConverter<java.util.Map<String, Object>, String> {

    private static final ObjectMapper om = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute == null ? null : om.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Serialize jsonData failed", e);
        }

    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            if(dbData == null || dbData.isBlank()) return java.util.Map.of();
            return om.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Deserialize jsonData failed", e);
        }
    }
}
