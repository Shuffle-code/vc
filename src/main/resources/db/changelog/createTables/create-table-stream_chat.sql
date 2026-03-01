CREATE TABLE IF NOT EXISTS stream_chat (
                                        ID BIGSERIAL NOT NULL PRIMARY KEY,
    title varchar (255) NULL,
    STATUS varchar(30) DEFAULT NULL,
    owner_id bigint DEFAULT NULL
    );