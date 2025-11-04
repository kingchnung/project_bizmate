package com.bizmate.hr.controller;


import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.hr.service.CertificateService;
import com.bizmate.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final EmployeeService employeeService;

    @GetMapping("/{empId}/employment")
    public ResponseEntity<byte[]> generateEmploymentCertificate(@PathVariable Long empId) {
        byte[] pdfBytes = certificateService.generateEmploymentCertificate(empId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "attachment",
                URLEncoder.encode("재직증명서_" + empId + ".pdf", StandardCharsets.UTF_8)
        );

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/mycertificates")
    public ResponseEntity<byte[]> downloadMyCertificate(Authentication auth) {
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long empId = user.getEmpId();

        byte[] pdfBytes = certificateService.generateEmploymentCertificate(empId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String fileName = "재직증명서_" + empId + ".pdf";
        headers.setContentDispositionFormData(
                "attachment",
                URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        );
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}

