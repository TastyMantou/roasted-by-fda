package com.roastedbyfda.backend.repository;

import com.roastedbyfda.backend.domain.WarningLetter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarningLetterRepository
        extends JpaRepository<WarningLetter, Long> {

    Optional<WarningLetter> findBySourceUrl(String sourceUrl);
}