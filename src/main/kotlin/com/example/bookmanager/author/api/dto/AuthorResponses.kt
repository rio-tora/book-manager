package com.example.bookmanager.author.api.dto

import com.example.bookmanager.book.api.dto.BookSummaryResponse
import com.example.bookmanager.author.domain.Author
import java.time.LocalDate

data class AuthorResponse(
    val id: Long,
    val name: String,
    val birthDate: LocalDate
)

fun Author.toResponse(): AuthorResponse =
    AuthorResponse(
        id = this.id,
        name = this.name,
        birthDate = this.birthDate
    )

data class AuthorBooksResponse(
    val author: AuthorResponse,
    val books: List<BookSummaryResponse>
)