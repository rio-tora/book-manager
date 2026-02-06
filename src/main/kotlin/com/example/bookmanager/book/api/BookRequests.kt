package com.example.bookmanager.book.api

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

enum class PublicationStatus {
    UNPUBLISHED,
    PUBLISHED
}

data class UpsertBookRequest(
    @field:NotBlank
    val title: String,

    @field:NotNull
    @field:DecimalMin(value = "0.0", inclusive = true)
    val price: BigDecimal,

    @field:Size(min = 1)
    val authorIds: List<Long>,

    @field:NotNull
    val publicationStatus: PublicationStatus
)
