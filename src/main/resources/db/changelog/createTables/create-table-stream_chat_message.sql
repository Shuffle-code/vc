CREATE TABLE IF NOT EXISTS stream_chat_message (
                                        ID BIGSERIAL NOT NULL PRIMARY KEY,
                                        stream_id bigint DEFAULT NULL,
    sender_id bigint DEFAULT NULL,
    content varchar (1000) NULL,
    STATUS varchar(30) DEFAULT NULL,
    time_stamp timestamp DEFAULT NULL,
    expires_at TIMESTAMP DEFAULT NULL,
    CONSTRAINT FK_stream_chat_message_stream_chat FOREIGN KEY (stream_id) REFERENCES stream_chat (id),
    CONSTRAINT FK_stream_chat_message_account_user FOREIGN KEY (sender_id) REFERENCES account_user (id)
    );

CREATE INDEX idx_stream_chat_message_expires_at ON stream_chat_message(expires_at);
