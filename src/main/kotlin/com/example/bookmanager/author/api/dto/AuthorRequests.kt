package com.example.bookmanager.author.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class UpsertAuthorRequest(
    @field:NotBlank
    val name: String,

    @field:NotNull
    @field:PastOrPresent
    val birthDate: LocalDate
)
