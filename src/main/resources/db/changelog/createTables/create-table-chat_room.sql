CREATE TABLE IF NOT EXISTS chat_room (
                                        ID BIGSERIAL NOT NULL PRIMARY KEY,
                                        chat_id bigint DEFAULT NULL,
    sender_id bigint DEFAULT NULL,
    recipient_id bigint DEFAULT NULL
    );