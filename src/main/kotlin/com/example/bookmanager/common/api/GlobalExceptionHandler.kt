package com.example.bookmanager.common.api

import com.example.bookmanager.common.exception.BusinessRuleViolationException
import com.example.bookmanager.common.exception.ResourceNotFoundException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ValidationErrorResponse(
    val message: String,
    val errors: List<FieldValidationError>
)

data class FieldValidationError(
    val field: String,
    val reason: String
)

data class ApiErrorResponse(
    val message: String
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.allErrors.map { err ->
            when (err) {
                is FieldError -> FieldValidationError(
                    field = err.field,
                    reason = err.defaultMessage ?: "invalid value"
                )
                else -> FieldValidationError(
                    field = err.objectName,
                    reason = err.defaultMessage ?: "invalid value"
                )
            }
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ValidationErrorResponse(
                    message = "Validation failed",
                    errors = errors
                )
            )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.constraintViolations.map { v ->
            FieldValidationError(
                field = v.propertyPath.toString(),
                reason = v.message
            )
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ValidationErrorResponse(
                    message = "Validation failed",
                    errors = errors
                )
            )
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<ApiErrorResponse>  {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse(message = ex.message ?: "Resource not found"))
    }

    @ExceptionHandler(BusinessRuleViolationException::class)
    fun handleBusinessRuleViolation(ex: BusinessRuleViolationException): ResponseEntity<ValidationErrorResponse> {
        val error = FieldValidationError(
            field = "business_rule",
            reason = ex.message ?: "Invalid operation"
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ValidationErrorResponse(
                    message = "Business rule violation",
                    errors = listOf(error)
                )
            )
    }
}