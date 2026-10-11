package com.roastedbyfda.backend.service;

import com.roastedbyfda.backend.domain.SourceVersion;
import com.roastedbyfda.backend.domain.WarningLetter;
import com.roastedbyfda.backend.ingestion.ImportResult;
import com.roastedbyfda.backend.ingestion.LetterInput;
import com.roastedbyfda.backend.repository.SourceVersionRepository;
import com.roastedbyfda.backend.repository.WarningLetterRepository;

import java.net.URI;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LetterIngestionService {

    private final WarningLetterRepository letterRepository;
    private final SourceVersionRepository versionRepository;
    private final HashService hashService;

    public LetterIngestionService(
            WarningLetterRepository letterRepository,
            SourceVersionRepository versionRepository,
            HashService hashService) {

        this.letterRepository = letterRepository;
        this.versionRepository = versionRepository;
        this.hashService = hashService;
    }

    @Transactional
    public ImportResult ingest(LetterInput input) {

        validate(input);

        String sourceUrl = input.sourceUrl().trim();
        String contentHash =
                hashService.sha256(input.originalText());

        var existing = letterRepository.findBySourceUrl(
                sourceUrl
        );

        boolean newLetter = existing.isEmpty();

        WarningLetter letter = existing.orElseGet(
                () -> new WarningLetter(
                        sourceUrl,
                        input.companyName(),
                        input.letterDate(),
                        input.issuingOffice()
                )
        );

        if (newLetter) {
            letter = letterRepository.saveAndFlush(letter);
        }

        boolean versionExists =
                versionRepository
                        .existsByWarningLetterIdAndContentHash(
                                letter.getId(),
                                contentHash
                        );

        if (versionExists) {
            return new ImportResult(
                    letter.getId(),
                    newLetter,
                    false,
                    contentHash
            );
        }

        SourceVersion version = new SourceVersion(
                letter,
                contentHash,
                input.originalText()
        );

        versionRepository.save(version);

        letter.setCurrentSourceHash(contentHash);
        letterRepository.save(letter);

        return new ImportResult(
                letter.getId(),
                newLetter,
                true,
                contentHash
        );
    }

    private void validate(LetterInput input) {

        if (input == null ||
                input.sourceUrl() == null ||
                input.companyName() == null ||
                input.companyName().isBlank() ||
                input.originalText() == null ||
                input.originalText().isBlank()) {

            throw new IllegalArgumentException(
                    "Required warning letter data is missing"
            );
        }

        URI uri;

        try {
            uri = URI.create(input.sourceUrl().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid source URL", e
            );
        }

        String host = uri.getHost();

        if (!"https".equalsIgnoreCase(uri.getScheme()) ||
                host == null ||
                !host.toLowerCase(Locale.ROOT)
                        .equals("www.fda.gov")) {

            throw new IllegalArgumentException(
                    "Source must be an HTTPS www.fda.gov URL"
            );
        }
    }
}