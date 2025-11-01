CREATE TABLE IF NOT EXISTS observer_image (
                                ID BIGSERIAL NOT NULL PRIMARY KEY ,
                                path varchar(512) NOT NULL,
                                observer_id bigint DEFAULT NULL,
                                CONSTRAINT FK_OBSERVER_IMAGE_OBSERVER FOREIGN KEY (observer_id) REFERENCES observer (id)
);
