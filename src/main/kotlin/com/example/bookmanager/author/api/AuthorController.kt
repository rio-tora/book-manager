package com.example.bookmanager.author.api

import com.example.bookmanager.author.api.dto.AuthorResponse
import com.example.bookmanager.author.api.dto.CreateAuthorRequest
import com.example.bookmanager.author.api.dto.UpdateAuthorRequest
import com.example.bookmanager.author.api.dto.toBookSummaryResponse
import com.example.bookmanager.author.api.dto.toResponse
import com.example.bookmanager.author.service.AuthorService
import com.example.bookmanager.book.api.dto.BookSummaryResponse
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

    @PatchMapping("/{id}")
    open fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateAuthorRequest
    ): AuthorResponse {
        val updated = authorService.update(
            id = id,
            name = request.name,
            birthDate = request.birthDate
        )
        return updated.toResponse()
    }

    @GetMapping("/{id}/books")
    fun getBooksByAuthor(@PathVariable id: Long): ResponseEntity<List<BookSummaryResponse>> {
        val books = authorService.findBooksByAuthorId(id)
        return ResponseEntity.ok(books.map { it.toBookSummaryResponse() })
    }
}
