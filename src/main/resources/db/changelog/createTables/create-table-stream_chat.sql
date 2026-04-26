CREATE TABLE IF NOT EXISTS STREAM_CHAT (
                                           ID BIGSERIAL NOT NULL PRIMARY KEY,
                                           title VARCHAR(255) NULL,
    STATUS VARCHAR(30) DEFAULT NULL,
    owner_id BIGINT DEFAULT NULL,
    tournament_id BIGINT UNIQUE,  -- UNIQUE гарантирует, что у турнира только один чат
    CONSTRAINT fk_stream_chat_tournament
    FOREIGN KEY (tournament_id)
    REFERENCES tournament(ID) ON DELETE CASCADE
    );