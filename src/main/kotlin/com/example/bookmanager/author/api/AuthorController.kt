package com.example.bookmanager.author.api

import com.example.bookmanager.author.api.dto.AuthorResponse
import com.example.bookmanager.author.api.dto.CreateAuthorRequest
import com.example.bookmanager.author.domain.Author
import com.example.bookmanager.author.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService
) {

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateAuthorRequest
    ): ResponseEntity<AuthorResponse> {
        val created = authorService.create(
            name = request.name,
            birthDate = request.birthDate
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toResponse())
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long
    ): AuthorResponse {
        val author = authorService.getById(id)
        return author.toResponse()
    }

    private fun Author.toResponse(): AuthorResponse =
        AuthorResponse(
            id = this.id,
            name = this.name,
            birthDate = this.birthDate
        )
}
