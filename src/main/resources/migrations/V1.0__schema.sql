CREATE TABLE example_entity
(
    id                     SERIAL PRIMARY KEY,
    name                   VARCHAR(255) NOT NULL,
    order                  INTEGER      NOT NULL,
    additional_information JSONB        NOT NULL,
    created_at             timestamp without time zone NOT NULL,
);