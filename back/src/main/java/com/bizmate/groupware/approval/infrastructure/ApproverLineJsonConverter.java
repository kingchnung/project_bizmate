package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ✅ ApproverLineJsonConverter
 * - List<ApproverStep> ↔ JSON 문자열 변환
 * - null, 빈 JSON([]) 대응
 */
@Slf4j
@Converter
public class ApproverLineJsonConverter implements AttributeConverter<List<ApproverStep>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())     // ✅ LocalDateTime 지원
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(List<ApproverStep> attribute) {
        try {
            if (attribute == null) return "[]";
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("❌ ApproverLine 직렬화 실패: {}", e.getMessage(), e);
            return "[]";
        }
    }

    @Override
    public List<ApproverStep> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank() || dbData.equals("null")) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(dbData, new TypeReference<List<ApproverStep>>() {});
        } catch (Exception e) {
            log.error("❌ ApproverLine 역직렬화 실패: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
