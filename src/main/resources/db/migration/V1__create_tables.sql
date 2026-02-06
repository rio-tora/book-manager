CREATE TABLE authors
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    birth_date DATE         NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE books
(
    id                 BIGSERIAL PRIMARY KEY,
    title              VARCHAR(255)   NOT NULL,
    price              NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    publication_status VARCHAR(20)    NOT NULL CHECK (publication_status IN ('UNPUBLISHED', 'PUBLISHED')),
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE book_authors
(
    book_id   BIGINT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES authors (id) ON DELETE RESTRICT,
    PRIMARY KEY (book_id, author_id)
);
