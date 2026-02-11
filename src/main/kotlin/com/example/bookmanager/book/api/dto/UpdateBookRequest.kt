package com.example.bookmanager.book.api.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class UpdateBookRequest(
    val title: String? = null,

    @field:DecimalMin(value = "0.0", inclusive = true, message = "price must be >= 0")
    val price: BigDecimal? = null,

    @field:Size(min = 1, message = "authorIds must contain at least one author id when provided")
    val authorIds: List<Long>? = null,

    @field:Pattern(
        regexp = "UNPUBLISHED|PUBLISHED",
        message = "publicationStatus must be UNPUBLISHED or PUBLISHED"
    )
    val publicationStatus: String? = null
)