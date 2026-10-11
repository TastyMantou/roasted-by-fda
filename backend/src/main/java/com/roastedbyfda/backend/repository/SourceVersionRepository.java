package com.roastedbyfda.backend.repository;

import com.roastedbyfda.backend.domain.SourceVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceVersionRepository
        extends JpaRepository<SourceVersion, Long> {

    boolean existsByWarningLetterIdAndContentHash(
            Long warningLetterId,
            String contentHash
    );

    long countByWarningLetterId(Long warningLetterId);
}