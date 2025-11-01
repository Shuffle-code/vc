create table IF NOT EXISTS observer(
    id BIGSERIAL          primary key,
    firstname          varchar(255)                    not null,
    patronymic         varchar(255)                    null,
    lastname           varchar(255)                    null,
    year_of_birth      int                             null,
    RTTW               decimal(10, 2)                  null,
    ID_TTWR            varchar(255)                    null,
    VERSION            int            default 0        null,
    CREATED_BY         varchar(255)                    null,
    CREATED_DATE       timestamp                       null,
    LAST_MODIFIED_BY   varchar(255)                    null,
    LAST_MODIFIED_DATE timestamp                       null,
    STATUS             varchar(30)    default 'ACTIVE' not null
);