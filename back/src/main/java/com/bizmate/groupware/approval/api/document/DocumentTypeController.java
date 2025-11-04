package com.bizmate.groupware.approval.api.document;

import com.bizmate.groupware.approval.domain.document.DocumentStatus;
import com.bizmate.groupware.approval.domain.document.DocumentType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enums")
public class DocumentTypeController {

    @GetMapping("/document-types")
    public ResponseEntity<List<Map<String, String>>> getDocumentTypes() {
        List<Map<String, String>> list = Arrays.stream(DocumentType.values())
                .map(type -> Map.of(
                        "code", type.getCode(),
                        "label", type.getLabel()
                ))
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/document-status")
    public List<Map<String, String>> getDocumentStatuses() {
        return Arrays.stream(DocumentStatus.values())
                .map(status -> Map.of(
                        "code", status.name(),
                        "label", status.getLabel()
                ))
                .toList();
    }
}
