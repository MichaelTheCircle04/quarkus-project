CREATE TABLE IF NOT EXISTS public.authors
(
    author_id integer NOT NULL DEFAULT nextval('authors_author_id_seq'::regclass),
    name character varying(128) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT authors_pkey PRIMARY KEY (author_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.authors
    OWNER to postgres;

ALTER SEQUENCE public.authors_author_id_seq
    OWNED BY public.authors.author_id;