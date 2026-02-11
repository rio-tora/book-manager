package com.example.bookmanager.author.api.dto

import com.example.bookmanager.book.api.dto.BookSummaryResponse
import com.example.bookmanager.author.domain.Author
import com.example.bookmanager.author.domain.AuthorBookSummary
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

fun AuthorBookSummary.toBookSummaryResponse(): BookSummaryResponse =
    BookSummaryResponse(
        id = id,
        title = title,
        price = price,
        publicationStatus = publicationStatus
    )