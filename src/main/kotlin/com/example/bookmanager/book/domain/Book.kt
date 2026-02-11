package com.example.bookmanager.book.domain

import java.math.BigDecimal

data class Book(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val publicationStatus: PublicationStatus,
    val authorIds: List<Long>
)