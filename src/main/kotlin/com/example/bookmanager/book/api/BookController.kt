package com.example.bookmanager.book.api

import com.example.bookmanager.book.api.dto.CreateBookRequest
import com.example.bookmanager.book.api.dto.UpdateBookRequest
import com.example.bookmanager.book.api.dto.toResponse
import com.example.bookmanager.book.domain.PublicationStatus
import com.example.bookmanager.book.service.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreateBookRequest): ResponseEntity<Any> {
        val created = bookService.create(
            title = request.title,
            price = request.price,
            publicationStatus = PublicationStatus.valueOf(request.publicationStatus),
            authorIds = request.authorIds
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toResponse())
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateBookRequest
    ): ResponseEntity<Any> {
        val updated = bookService.update(
            id = id,
            title = request.title,
            price = request.price,
            publicationStatus = request.publicationStatus?.let { PublicationStatus.valueOf(it) },
            authorIds = request.authorIds
        )
        return ResponseEntity.ok(updated.toResponse())
    }
}