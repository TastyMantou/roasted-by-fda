package com.roastedbyfda.backend.ingestion;

import com.roastedbyfda.backend.service.LetterIngestionService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Profile("import-demo")
public class LocalImportRunner implements CommandLineRunner {

    private final LetterIngestionService service;

    public LocalImportRunner(LetterIngestionService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {

        var resource = new ClassPathResource(
                "sample-letter.txt"
        );

        String text;

        try (var stream = resource.getInputStream()) {
            text = new String(
                    stream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        LetterInput input = new LetterInput(
                "https://www.fda.gov/test-fixture-local-only",
                "Synthetic Test Company",
                LocalDate.of(2026, 1, 1),
                "Synthetic Testing Office",
                text,
                "Synthetic Test Subject"
        );

        ImportResult result = service.ingest(input);

        System.out.println("IMPORT RESULT: " + result);
    }
}