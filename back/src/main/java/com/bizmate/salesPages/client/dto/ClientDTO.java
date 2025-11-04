package com.bizmate.salesPages.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private Long clientNo;
    private String clientId;
    private String clientCompany;
    private String clientCeo;
    private String clientBusinessType;
    private String clientAddress;
    private String clientContact;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    private String clientNote;
    private String businessLicenseFile;
    private Boolean validationStatus;
    private String writer;
    private String clientEmail;
    private String userId;
}
