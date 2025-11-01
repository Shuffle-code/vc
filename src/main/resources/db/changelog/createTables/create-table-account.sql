create table IF NOT EXISTS account_role
(
    ID BIGSERIAL NOT NULL  primary key,
    name varchar(255) not null,
    constraint name
        unique (name)
);
