-- 著者テーブル
CREATE TABLE authors
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    birth_date DATE         NOT NULL
);

-- 書籍テーブル
CREATE TABLE books
(
    id     SERIAL PRIMARY KEY,
    title  VARCHAR(255) NOT NULL,
    price  INT          NOT NULL CHECK (price >= 0),
    status VARCHAR(50)  NOT NULL
);

-- 書籍と著者の紐付け（多対多）テーブル
CREATE TABLE book_authors
(
    book_id   INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE
);