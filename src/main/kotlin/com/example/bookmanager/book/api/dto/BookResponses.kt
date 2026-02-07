package com.example.bookmanager.book.api

import java.math.BigDecimal

data class BookResponse(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val publicationStatus: PublicationStatus,
    val authorIds: List<Long>
)

data class BookSummaryResponse(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val publicationStatus: PublicationStatus
)