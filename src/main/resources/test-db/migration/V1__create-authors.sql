CREATE SEQUENCE IF NOT EXISTS authors_author_id_seq
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

CREATE TABLE IF NOT EXISTS "authors"
(
    "author_id" integer NOT NULL DEFAULT nextval('authors_author_id_seq'),
    "name" VARCHAR(128) NOT NULL,
    CONSTRAINT authors_pkey PRIMARY KEY ("author_id")
)