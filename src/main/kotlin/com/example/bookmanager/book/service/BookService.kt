package com.example.bookmanager.book.service

import com.example.bookmanager.author.service.AuthorService
import com.example.bookmanager.book.domain.Book
import com.example.bookmanager.book.domain.PublicationStatus
import com.example.bookmanager.book.domain.repository.BookRepository
import com.example.bookmanager.common.exception.BusinessRuleViolationException
import com.example.bookmanager.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorService: AuthorService
) {

    @Transactional
    fun create(
        title: String,
        price: BigDecimal,
        publicationStatus: PublicationStatus,
        authorIds: List<Long>
    ): Book {
        authorService.validateAllAuthorsExist(authorIds)

        return try {
            bookRepository.create(
                title = title,
                price = price,
                publicationStatus = publicationStatus,
                authorIds = authorIds
            )
        } catch (e: IllegalArgumentException) {
            throw BusinessRuleViolationException(e.message ?: "Invalid request")
        }
    }

    @Transactional
    open fun update(
        id: Long,
        title: String?,
        price: BigDecimal?,
        publicationStatus: PublicationStatus?,
        authorIds: List<Long>?
    ): Book {
        if (title != null && title.isBlank()) {
            throw BusinessRuleViolationException("title must not be blank")
        }

        authorIds?.let { authorService.validateAllAuthorsExist(it) }

        return try {
            bookRepository.update(
                id = id,
                title = title,
                price = price,
                publicationStatus = publicationStatus,
                authorIds = authorIds
            ) ?: throw ResourceNotFoundException("Book", id)
        } catch (e: IllegalArgumentException) {
            throw BusinessRuleViolationException(e.message ?: "Invalid request")
        } catch (e: NoSuchElementException) {
            throw ResourceNotFoundException("Book", id)
        }
    }
}