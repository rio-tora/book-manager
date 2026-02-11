package com.example.bookmanager.author.domain.repository

import com.example.bookmanager.author.domain.Author
import com.example.bookmanager.author.domain.AuthorBookSummary
import com.example.bookmanager.book.domain.PublicationStatus
import com.example.bookmanager.jooq.Tables.AUTHORS
import com.example.bookmanager.jooq.Tables.BOOK_AUTHORS
import com.example.bookmanager.jooq.tables.Books.BOOKS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class JooqAuthorRepository(
    private val dsl: DSLContext
) : AuthorRepository {

    override fun create(name: String, birthDate: LocalDate): Author {
        val record = dsl.insertInto(AUTHORS)
            .set(AUTHORS.NAME, name)
            .set(AUTHORS.BIRTH_DATE, birthDate)
            .returning(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .fetchOne()
            ?: error("Failed to create author")

        return Author(
            id = record.get(AUTHORS.ID)!!,
            name = record.get(AUTHORS.NAME)!!,
            birthDate = record.get(AUTHORS.BIRTH_DATE)!!
        )
    }

    override fun findById(id: Long): Author? {
        val record = dsl.select(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .from(AUTHORS)
            .where(AUTHORS.ID.eq(id))
            .fetchOne() ?: return null

        return Author(
            id = record.get(AUTHORS.ID)!!,
            name = record.get(AUTHORS.NAME)!!,
            birthDate = record.get(AUTHORS.BIRTH_DATE)!!
        )
    }

    override fun update(id: Long, name: String?, birthDate: LocalDate?): Author? {
        val current = dsl
            .select(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .from(AUTHORS)
            .where(AUTHORS.ID.eq(id))
            .fetchOne() ?: return null

        val nextName = name ?: current.get(AUTHORS.NAME) ?: error("name is null")
        val nextBirthDate = birthDate ?: current.get(AUTHORS.BIRTH_DATE) ?: error("birth_date is null")

        dsl.update(AUTHORS)
            .set(AUTHORS.NAME, nextName)
            .set(AUTHORS.BIRTH_DATE, nextBirthDate)
            .where(AUTHORS.ID.eq(id))
            .execute()

        return Author(
            id = id,
            name = nextName,
            birthDate = nextBirthDate
        )
    }

    override fun existsAllByIds(ids: Collection<Long>): Boolean {
        if (ids.isEmpty()) return false
        val count = dsl.selectCount()
            .from(AUTHORS)
            .where(AUTHORS.ID.`in`(ids))
            .fetchOne(0, Int::class.java) ?: 0
        return count == ids.size
    }

    override fun findBooksByAuthorId(authorId: Long): List<AuthorBookSummary> {
        return dsl
            .select(
                BOOKS.ID,
                BOOKS.TITLE,
                BOOKS.PRICE,
                BOOKS.PUBLICATION_STATUS
            )
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOKS.ID.eq(BOOK_AUTHORS.BOOK_ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .orderBy(BOOKS.ID.asc())
            .fetch { record ->
                AuthorBookSummary(
                    id = record.get(BOOKS.ID)!!,
                    title = record.get(BOOKS.TITLE)!!,
                    price = record.get(BOOKS.PRICE)!!,
                    publicationStatus = PublicationStatus.valueOf(record.get(BOOKS.PUBLICATION_STATUS)!!)
                )
            }
    }
}