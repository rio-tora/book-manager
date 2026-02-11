package com.example.bookmanager.author.api.dto

import jakarta.validation.constraints.Size
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class AuthorRequest(
    val name: String,
    @field:PastOrPresent(message = "birthDate must be today or in the past")
    val birthDate: LocalDate
)

data class UpdateAuthorRequest(
    @field:Size(max = 255, message = "name must be at most 255 characters")
    val name: String?,
    @field:PastOrPresent(message = "birthDate must be today or in the past")
    val birthDate: LocalDate?
)
