-- Table: public.books

-- DROP TABLE IF EXISTS public.books;

CREATE TABLE IF NOT EXISTS public.books
(
    book_id bigint NOT NULL DEFAULT nextval('books_book_id_seq'::regclass),
    title character varying(128) COLLATE pg_catalog."default" NOT NULL,
    price integer NOT NULL,
    amount integer NOT NULL,
    author_id integer NOT NULL,
    CONSTRAINT books_pkey PRIMARY KEY (book_id),
    CONSTRAINT books_author_id_fkey FOREIGN KEY (author_id)
        REFERENCES public.authors (author_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.books
    OWNER to postgres;