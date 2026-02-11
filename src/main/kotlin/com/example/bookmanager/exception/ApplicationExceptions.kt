package com.example.bookmanager.common.exception

open class ApplicationException(
    message: String
) : RuntimeException(message)

class ResourceNotFoundException(
    resourceName: String,
    id: Any
) : ApplicationException("$resourceName not found. id=$id")

class BusinessRuleViolationException(
    message: String
) : ApplicationException(message)
