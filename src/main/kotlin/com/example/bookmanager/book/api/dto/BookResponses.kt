package com.example.bookmanager.book.api.dto

import com.example.bookmanager.book.domain.Book
import com.example.bookmanager.book.domain.PublicationStatus
import java.math.BigDecimal

data class BookResponse(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val publicationStatus: PublicationStatus,
    val authorIds: List<Long>
)


fun Book.toResponse(): BookResponse =
    BookResponse(
        id = id,
        title = title,
        price = price,
        publicationStatus = publicationStatus,
        authorIds = authorIds
    )

data class BookSummaryResponse(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val publicationStatus: PublicationStatus
)