CREATE TABLE IF NOT EXISTS account_role (
                                ID BIGSERIAL NOT NULL PRIMARY KEY,
                                name varchar(255) NOT NULL UNIQUE
);

