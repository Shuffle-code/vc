--liquibase formatted sql
--changeset liquibase:ea440c5f-3287-4e9d-9ae8-038a403ec034

CREATE TABLE IF NOT EXISTS tour_image (
                                          ID BIGSERIAL NOT NULL PRIMARY KEY,
                                          path varchar(512) NOT NULL,
    thumbnail_path varchar(512) NOT NULL,
    tour_id bigint DEFAULT NULL,
    CONSTRAINT FK_TOUR_IMAGE_TOURNAMENT FOREIGN KEY (tour_id) REFERENCES tournament (id)
    );


