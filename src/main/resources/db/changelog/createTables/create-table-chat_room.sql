CREATE TABLE IF NOT EXISTS chat_room (
                                        ID bigint NOT NULL PRIMARY KEY,
                                        chat_id varchar (255) NULL,
    sender_id varchar (255) NULL,
    recipient_id varchar (255) NULL
    );