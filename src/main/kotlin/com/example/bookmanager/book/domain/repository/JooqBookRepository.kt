package com.example.bookmanager.book.domain.repository

import com.example.bookmanager.book.domain.Book
import com.example.bookmanager.book.domain.BookSummary
import com.example.bookmanager.book.domain.PublicationStatus
import com.example.bookmanager.jooq.Tables.BOOKS
import com.example.bookmanager.jooq.Tables.BOOK_AUTHORS
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class JooqBookRepository(
    private val dsl: DSLContext
) : BookRepository {

    override fun create(
        title: String,
        price: BigDecimal,
        publicationStatus: PublicationStatus,
        authorIds: List<Long>
    ): Book {
        return dsl.transactionResult { configuration ->
            val tx = DSL.using(configuration)

            val bookId = tx.insertInto(BOOKS)
                .set(BOOKS.TITLE, title)
                .set(BOOKS.PRICE, price)
                .set(BOOKS.PUBLICATION_STATUS, publicationStatus.name)
                .returning(BOOKS.ID)
                .fetchOne()
                ?.get(BOOKS.ID)
                ?: error("failed to insert book")

            authorIds.distinct().forEach { authorId ->
                tx.insertInto(BOOK_AUTHORS)
                    .set(BOOK_AUTHORS.BOOK_ID, bookId)
                    .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                    .execute()
            }

            Book(
                id = bookId,
                title = title,
                price = price,
                publicationStatus = publicationStatus,
                authorIds = authorIds.distinct()
            )
        }
    }

    override fun update(
        id: Long,
        title: String?,
        price: BigDecimal?,
        publicationStatus: PublicationStatus?,
        authorIds: List<Long>?
    ): Book? {
        return dsl.transactionResult { configuration ->
            val tx = DSL.using(configuration)

            val current = tx.select(
                BOOKS.ID,
                BOOKS.TITLE,
                BOOKS.PRICE,
                BOOKS.PUBLICATION_STATUS
            )
                .from(BOOKS)
                .where(BOOKS.ID.eq(id))
                .fetchOne()
                ?: return@transactionResult null

            val currentStatus = PublicationStatus.valueOf(
                current.get(BOOKS.PUBLICATION_STATUS) ?: error("publication status is null")
            )
            val nextStatus = publicationStatus ?: currentStatus

            // 仕様: PUBLISHED -> UNPUBLISHED は禁止
            if (currentStatus == PublicationStatus.PUBLISHED && nextStatus == PublicationStatus.UNPUBLISHED) {
                throw IllegalArgumentException("published book cannot be changed to unpublished")
            }

            val nextTitle = title ?: (current.get(BOOKS.TITLE) ?: error("title is null"))
            val nextPrice = price ?: (current.get(BOOKS.PRICE) ?: error("price is null"))

            tx.update(BOOKS)
                .set(BOOKS.TITLE, nextTitle)
                .set(BOOKS.PRICE, nextPrice)
                .set(BOOKS.PUBLICATION_STATUS, nextStatus.name)
                .where(BOOKS.ID.eq(id))
                .execute()

            if (authorIds != null) {
                val distinctAuthorIds = authorIds.distinct()

                if (distinctAuthorIds.isEmpty()) {
                    throw IllegalArgumentException("book must have at least one author id")
                }

                tx.deleteFrom(BOOK_AUTHORS)
                    .where(BOOK_AUTHORS.BOOK_ID.eq(id))
                    .execute()

                distinctAuthorIds.forEach { authorId ->
                    tx.insertInto(BOOK_AUTHORS)
                        .set(BOOK_AUTHORS.BOOK_ID, id)
                        .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                        .execute()
                }
            }

            val finalAuthorIds = tx.select(BOOK_AUTHORS.AUTHOR_ID)
                .from(BOOK_AUTHORS)
                .where(BOOK_AUTHORS.BOOK_ID.eq(id))
                .orderBy(BOOK_AUTHORS.AUTHOR_ID.asc())
                .fetch(BOOK_AUTHORS.AUTHOR_ID)

            Book(
                id = id,
                title = nextTitle,
                price = nextPrice,
                publicationStatus = nextStatus,
                authorIds = finalAuthorIds
            )
        }
    }

    override fun findById(id: Long): Book? {
        val bookRecord = dsl.select(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLICATION_STATUS
        )
            .from(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne()
            ?: return null

        val authorIds = dsl.select(BOOK_AUTHORS.AUTHOR_ID)
            .from(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(id))
            .orderBy(BOOK_AUTHORS.AUTHOR_ID.asc())
            .fetch(BOOK_AUTHORS.AUTHOR_ID)

        return Book(
            id = bookRecord.get(BOOKS.ID) ?: error("book id is null"),
            title = bookRecord.get(BOOKS.TITLE) ?: error("book title is null"),
            price = bookRecord.get(BOOKS.PRICE) ?: error("book price is null"),
            publicationStatus = PublicationStatus.valueOf(
                bookRecord.get(BOOKS.PUBLICATION_STATUS) ?: error("publication status is null")
            ),
            authorIds = authorIds
        )
    }

    override fun findByAuthorId(authorId: Long): List<Book> {
        val bookRows = dsl.selectDistinct(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLICATION_STATUS
        )
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOK_AUTHORS.BOOK_ID.eq(BOOKS.ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .orderBy(BOOKS.ID.asc())
            .fetch()

        if (bookRows.isEmpty()) return emptyList()

        val bookIds = bookRows.mapNotNull { it.get(BOOKS.ID) }

        val authorMap = dsl.select(BOOK_AUTHORS.BOOK_ID, BOOK_AUTHORS.AUTHOR_ID)
            .from(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.`in`(bookIds))
            .fetch()
            .groupBy(
                { it.get(BOOK_AUTHORS.BOOK_ID) ?: error("book_id is null") },
                { it.get(BOOK_AUTHORS.AUTHOR_ID) ?: error("author_id is null") }
            )

        return bookRows.map { row ->
            val bookId = row.get(BOOKS.ID) ?: error("book id is null")
            Book(
                id = bookId,
                title = row.get(BOOKS.TITLE) ?: error("book title is null"),
                price = row.get(BOOKS.PRICE) ?: error("book price is null"),
                publicationStatus = PublicationStatus.valueOf(
                    row.get(BOOKS.PUBLICATION_STATUS) ?: error("publication status is null")
                ),
                authorIds = authorMap[bookId]?.sorted() ?: emptyList()
            )
        }
    }

    override fun findSummariesByAuthorId(authorId: Long): List<BookSummary> {
        return dsl.select(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLICATION_STATUS
        )
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOK_AUTHORS.BOOK_ID.eq(BOOKS.ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch { record ->
                BookSummary(
                    id = record.get(BOOKS.ID)!!,
                    title = record.get(BOOKS.TITLE)!!,
                    price = record.get(BOOKS.PRICE)!!,
                    publicationStatus = PublicationStatus.valueOf(record.get(BOOKS.PUBLICATION_STATUS)!!)
                )
            }
    }
}