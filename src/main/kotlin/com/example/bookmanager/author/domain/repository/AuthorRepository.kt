package com.example.bookmanager.author.domain.repository

import com.example.bookmanager.author.domain.Author
import java.time.LocalDate

interface AuthorRepository {
    fun create(name: String, birthDate: LocalDate): Author
    fun findById(id: Long): Author?
    fun findAllByIds(ids: Collection<Long>): List<Author>
    fun existsAllByIds(ids: Collection<Long>): Boolean
}