package com.example.bookmanager.author.service

import com.example.bookmanager.author.domain.Author
import com.example.bookmanager.author.domain.repository.AuthorRepository
import com.example.bookmanager.common.exception.BusinessRuleViolationException
import com.example.bookmanager.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuthorService(
    private val authorRepository: AuthorRepository
) {
    fun create(name: String, birthDate: LocalDate): Author {
        if (birthDate.isAfter(LocalDate.now())) {
            throw BusinessRuleViolationException("birthDate must be today or in the past")
        }
        return authorRepository.create(name = name, birthDate = birthDate)
    }

    fun getById(id: Long): Author {
        return authorRepository.findById(id)
            ?: throw ResourceNotFoundException("Author", id)
    }

    fun validateAllAuthorsExist(authorIds: Collection<Long>) {
        val distinctIds = authorIds.toSet()
        if (distinctIds.isEmpty()) {
            throw BusinessRuleViolationException("At least one author is required")
        }
        if (!authorRepository.existsAllByIds(distinctIds)) {
            throw BusinessRuleViolationException("One or more authors do not exist")
        }
    }

    fun findAllByIds(authorIds: Collection<Long>): List<Author> {
        val distinctIds = authorIds.toSet()
        return authorRepository.findAllByIds(distinctIds)
    }
}