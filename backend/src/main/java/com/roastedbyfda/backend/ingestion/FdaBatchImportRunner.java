package com.roastedbyfda.backend.ingestion;

import com.roastedbyfda.backend.service.LetterIngestionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Profile("real-import")
public class FdaBatchImportRunner implements CommandLineRunner {

    private final LetterIngestionService ingestionService;
    private final Path importDirectory;

    private static final DateTimeFormatter FDA_DATE_FORMAT =
            DateTimeFormatter.ofPattern("M/d/uuuu", Locale.US);

    public FdaBatchImportRunner(
            LetterIngestionService ingestionService,
            @Value("${fda.import.directory}") String directory) {

        this.ingestionService = ingestionService;
        this.importDirectory = Path.of(directory)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public void run(String... args) throws Exception {

        Path manifest = importDirectory.resolve("manifest.tsv");

        if (!Files.isRegularFile(manifest)) {
            throw new IllegalStateException(
                    "Manifest not found: " + manifest
            );
        }

        List<String> lines = Files.readAllLines(
                manifest,
                StandardCharsets.UTF_8
        );

        if (lines.isEmpty()) {
            throw new IllegalStateException(
                    "Manifest is empty"
            );
        }

        String[] headers = lines.get(0).split("\t", -1);

        if (headers.length != 6) {
            throw new IllegalArgumentException(
                    "Manifest must contain six columns"
            );
        }

        int newVersions = 0;
        int unchanged = 0;
        int failed = 0;

        for (int i = 1; i < lines.size(); i++) {

            if (lines.get(i).isBlank()) {
                continue;
            }

            try {
                String[] fields = lines.get(i).split("\t", -1);

                if (fields.length != 6) {
                    throw new IllegalArgumentException(
                            "Expected six columns on line " + (i + 1)
                    );
                }

                String filename = fields[0].trim();

                Path file = importDirectory
                        .resolve(filename)
                        .normalize();

                if (!file.startsWith(importDirectory)
                        || !Files.isRegularFile(file)) {
                    throw new IllegalArgumentException(
                            "Invalid or missing file: " + filename
                    );
                }

                String originalText = Files.readString(
                        file,
                        StandardCharsets.UTF_8
                );

                LetterInput input = new LetterInput(
                        fields[1].trim(),
                        fields[2].trim(),
                        LocalDate.parse(fields[3].trim(), FDA_DATE_FORMAT),
                        fields[4].trim(),
                        originalText,
                        fields[5].trim()
                );

                ImportResult result = ingestionService.ingest(input);

                if (result.newVersion()) {
                    newVersions++;
                } else {
                    unchanged++;
                }

                System.out.printf(
                        "IMPORTED %s | letterId=%d | newLetter=%s | newVersion=%s%n",
                        filename,
                        result.letterId(),
                        result.newLetter(),
                        result.newVersion()
                );

            } catch (Exception e) {
                failed++;

                System.err.printf(
                        "FAILED line %d: %s%n",
                        i + 1,
                        e.getMessage()
                );
            }
        }

        System.out.printf(
                "BATCH COMPLETE | newVersions=%d | unchanged=%d | failed=%d%n",
                newVersions,
                unchanged,
                failed
        );

        if (failed > 0) {
            throw new IllegalStateException(
                    "Batch import finished with " + failed + " errors"
            );
        }
    }
}