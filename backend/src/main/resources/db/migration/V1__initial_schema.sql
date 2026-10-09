CREATE TABLE warning_letter (
                                id BIGSERIAL PRIMARY KEY,
                                source_url TEXT NOT NULL UNIQUE,
                                company_name VARCHAR(300) NOT NULL,
                                letter_date DATE,
                                issuing_office VARCHAR(300),
                                current_source_hash VARCHAR(64),
                                status VARCHAR(40) NOT NULL DEFAULT 'INGESTED',
                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE source_version (
                                id BIGSERIAL PRIMARY KEY,
                                warning_letter_id BIGINT NOT NULL
                                    REFERENCES warning_letter(id),
                                content_hash VARCHAR(64) NOT NULL,
                                original_text TEXT NOT NULL,
                                fetched_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                UNIQUE (warning_letter_id, content_hash)
);