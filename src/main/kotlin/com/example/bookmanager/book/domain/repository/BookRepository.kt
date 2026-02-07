package com.example.bookmanager.book.domain.repository

import com.example.bookmanager.book.domain.Book
import com.example.bookmanager.book.domain.BookSummary
import com.example.bookmanager.book.domain.PublicationStatus
import java.math.BigDecimal

interface BookRepository {
    fun create(
        title: String,
        price: BigDecimal,
        publicationStatus: PublicationStatus,
        authorIds: List<Long>
    ): Book

    fun update(
        id: Long,
        title: String?,
        price: BigDecimal?,
        publicationStatus: PublicationStatus?,
        authorIds: List<Long>?
    ): Book?

    fun findById(id: Long): Book?

    fun findByAuthorId(authorId: Long): List<Book>

    fun findSummariesByAuthorId(authorId: Long): List<BookSummary>
}