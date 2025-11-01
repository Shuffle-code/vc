CREATE TABLE IF NOT EXISTS authority (
                             ID bigint NOT NULL PRIMARY KEY,
                             permission varchar(255) NOT NULL UNIQUE
);