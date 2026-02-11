package com.example.bookmanager.book.api.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import java.math.BigDecimal

data class CreateBookRequest(
    @field:NotBlank(message = "title is required")
    val title: String,

    @field:DecimalMin(value = "0.0", inclusive = true, message = "price must be >= 0")
    val price: BigDecimal,

    @field:NotEmpty(message = "authorIds must contain at least one author id")
    val authorIds: List<Long>,

    @field:Pattern(
        regexp = "UNPUBLISHED|PUBLISHED",
        message = "publicationStatus must be UNPUBLISHED or PUBLISHED"
    )
    val publicationStatus: String
)
