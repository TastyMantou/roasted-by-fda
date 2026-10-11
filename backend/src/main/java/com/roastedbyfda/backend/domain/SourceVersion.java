package com.roastedbyfda.backend.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "source_version",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"warning_letter_id", "content_hash"}
        )
)
public class SourceVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warning_letter_id", nullable = false)
    private WarningLetter warningLetter;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "original_text", nullable = false,
            columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "fetched_at",
            insertable = false, updatable = false)
    private OffsetDateTime fetchedAt;

    protected SourceVersion() {}

    public SourceVersion(
            WarningLetter warningLetter,
            String contentHash,
            String originalText) {

        this.warningLetter = warningLetter;
        this.contentHash = contentHash;
        this.originalText = originalText;
    }
}