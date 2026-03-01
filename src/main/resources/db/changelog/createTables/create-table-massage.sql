CREATE TABLE IF NOT EXISTS message (
                             ID BIGSERIAL PRIMARY KEY,
                             content varchar (1000) NULL,
                             timestamp timestamp null,
                             account_user_id bigint DEFAULT NULL,
    CONSTRAINT FK_MESSAGES_ACCOUNT_USER_ID FOREIGN KEY (account_user_id) REFERENCES account_user (id)
    );
