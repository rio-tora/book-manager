package com.example.bookmanager.author.domain

import com.example.bookmanager.book.domain.PublicationStatus
import java.math.BigDecimal

data class AuthorBookSummary(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val publicationStatus: PublicationStatus
)