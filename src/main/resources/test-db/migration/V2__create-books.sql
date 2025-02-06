CREATE SEQUENCE IF NOT EXISTS books_book_id_seq
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS "books"
(
    "book_id" bigint NOT NULL DEFAULT nextval('books_book_id_seq'),
    "title" VARCHAR(128) NOT NULL,
    "price" integer NOT NULL,
    "amount" integer NOT NULL,
    "author_id" integer NOT NULL,
    CONSTRAINT books_pkey PRIMARY KEY ("book_id"),
    CONSTRAINT books_author_id_fkey FOREIGN KEY ("author_id")
        REFERENCES "authors" ("author_id") 
        ON UPDATE CASCADE
        ON DELETE CASCADE
)