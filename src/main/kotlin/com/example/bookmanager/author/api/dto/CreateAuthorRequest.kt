package com.example.bookmanager.author.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateAuthorRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val name: String,

    @field:NotNull
    val birthDate: LocalDate
)