package com.roastedbyfda.backend.ingestion;

public record ImportResult(
        Long letterId,
        boolean newLetter,
        boolean newVersion,
        String contentHash
) {}