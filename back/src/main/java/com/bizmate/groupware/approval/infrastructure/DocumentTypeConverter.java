package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.groupware.approval.domain.document.DocumentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = false)
public class DocumentTypeConverter implements AttributeConverter<DocumentType, String> {

    @Override
    public String convertToDatabaseColumn(DocumentType attribute) {
        if (attribute == null) return null;
        return attribute.name(); // DB에는 Enum name 그대로 저장
    }

    @Override
    public DocumentType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return DocumentType.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Unknown DocumentType value in DB: {}", dbData);
            return null;
        }
    }
}