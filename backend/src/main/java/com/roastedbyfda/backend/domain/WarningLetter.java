package com.roastedbyfda.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "warning_letter")
public class WarningLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_url", nullable = false,
            unique = true, columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(name = "company_name", nullable = false,
            length = 300)
    private String companyName;

    @Column(name = "letter_date")
    private LocalDate letterDate;

    @Column(name = "issuing_office", length = 300)
    private String issuingOffice;

    @Column(name = "current_source_hash", length = 64)
    private String currentSourceHash;

    @Column(name = "status", nullable = false, length = 40)
    private String status = "INGESTED";

    @Column(name = "created_at",
            insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at",
            insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "subject", columnDefinition = "TEXT")
    private String subject;

    protected WarningLetter() {}

    public WarningLetter(
            String sourceUrl,
            String companyName,
            LocalDate letterDate,
            String issuingOffice,
            String subject) {

        this.sourceUrl = sourceUrl;
        this.companyName = companyName;
        this.letterDate = letterDate;
        this.issuingOffice = issuingOffice;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public String getCurrentSourceHash() {
        return currentSourceHash;
    }

    public void setCurrentSourceHash(String hash) {
        this.currentSourceHash = hash;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}