CREATE TABLE example_entity
(
    id                     SERIAL PRIMARY KEY,
    name                   VARCHAR(255) UNIQUE NOT NULL,
    index                  INTEGER             NOT NULL,
    additional_information JSONB               NOT NULL,
    created_at             timestamp without time zone NOT NULL
);