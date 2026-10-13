package com.roastedbyfda.backend.ingestion;

import java.time.LocalDate;

public record LetterInput(
        String sourceUrl,
        String companyName,
        LocalDate letterDate,
        String issuingOffice,
        String originalText,
        String subject

) {}