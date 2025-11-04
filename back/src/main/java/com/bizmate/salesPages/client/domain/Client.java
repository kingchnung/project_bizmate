package com.bizmate.salesPages.client.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CLIENT")
@Audited
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENT_SEQ_GENERATOR")
    @SequenceGenerator(
            name = "CLIENT_SEQ_GENERATOR",
            sequenceName = "CLIENT_SEQ",
            allocationSize = 1,
            initialValue = 1
    )
    private Long clientNo;

    @Column(unique = true, nullable = false)
    private String clientId;

    private String clientCompany;
    private String clientCeo;
    private String clientBusinessType;
    private String clientAddress;
    private String clientContact;
    private String clientEmail;
    private String writer;
    private String userId;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private LocalDate registrationDate;

    private String clientNote;
    private String businessLicenseFile;

    @Builder.Default
    private Boolean validationStatus = false;


    public void changeClientId(String clientId){
        this.clientId = clientId;
    }
    public void changeClientCompany(String clientCompany){
        this.clientCompany = clientCompany;
    }
    public void changeClientCeo(String clientCeo){
        this.clientCeo = clientCeo;
    }
    public void changeClientBusinessType(String clientBusinessType){
        this.clientBusinessType = clientBusinessType;
    }
    public void changeClientAddress(String clientAddress){
        this.clientAddress = clientAddress;
    }
    public void changeClientContact(String clientContact) {
        this.clientContact = clientContact;
    }
    public void changeClientNote(String clientNote) {
        this.clientNote = clientNote;
    }
    public void changeBusinessLicenseFile(String businessLicenseFile) {
        this.businessLicenseFile = businessLicenseFile;
    }
    public void changeClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
}
