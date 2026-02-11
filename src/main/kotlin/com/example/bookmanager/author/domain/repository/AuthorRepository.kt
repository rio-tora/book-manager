package com.example.bookmanager.author.domain.repository

import com.example.bookmanager.author.domain.Author
import com.example.bookmanager.author.domain.AuthorBookSummary
import java.time.LocalDate

interface AuthorRepository {
    fun create(name: String, birthDate: LocalDate): Author
    fun findById(id: Long): Author?
    fun update(id: Long, name: String?, birthDate: LocalDate?): Author?
    fun existsAllByIds(ids: Collection<Long>): Boolean
    fun findBooksByAuthorId(authorId: Long): List<AuthorBookSummary>
}