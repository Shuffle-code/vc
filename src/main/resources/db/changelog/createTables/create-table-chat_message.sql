CREATE TABLE IF NOT EXISTS chat_message (
                                        ID bigint NOT NULL PRIMARY KEY,
    sender_id varchar (255) NULL,
    recipient_id varchar (255) NULL,
    content varchar (255) NULL,
    time_stamp timestamp DEFAULT NULL
    );
