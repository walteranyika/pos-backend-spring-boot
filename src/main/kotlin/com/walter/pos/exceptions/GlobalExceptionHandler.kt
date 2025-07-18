package com.walter.pos.exceptions

import com.walter.pos.dtos.ErrorResponse
import io.jsonwebtoken.MalformedJwtException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime
import java.util.regex.Pattern

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    // A pattern to extract the duplicate value from the MySQL exception message.
    private val duplicateEntryPattern = Pattern.compile("Duplicate entry '([^']*)'")

    /**
     * Handles the custom ResourceNotFoundException and returns a 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val path = (request as ServletWebRequest).request.requestURI

        logger.warn("Resource not found exception for path '{}': {}", path, ex.message)

        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "The requested resource was not found.",
            path = path
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(
        ex: BadCredentialsException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val path = (request as ServletWebRequest).request.requestURI

        logger.warn("Wrong username or password'{}': {}", path, ex.message)

        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Bad credentials",
            message = ex.message ?: "Wrong username or password.",
            path = path
        )
        return ResponseEntity(errorDetails, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(MalformedJwtException::class)
    fun handleJwtMalformedException(
        ex: MalformedJwtException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val path = (request as ServletWebRequest).request.requestURI

        logger.warn("MalformedJwtException Token '{}': {}", path, ex.message)

        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.FORBIDDEN.value(),
            error = "Invalid Token",
            message = ex.message ?: "Invalid Token.",
            path = path
        )
        return ResponseEntity(errorDetails, HttpStatus.FORBIDDEN)
    }


    /**
     * Handles database integrity constraint violations, such as duplicate unique keys.
     * This handler is more robust as it catches Spring's generic `DataIntegrityViolationException`
     * and then inspects the root cause to provide a specific, user-friendly message.
     * It returns a 409 Conflict status.
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(
        ex: DataIntegrityViolationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {

        val rootCause = ex.rootCause
        var userFriendlyMessage = "A database integrity constraint was violated. Please check your input."

        // Check the root cause for the specific MySQL duplicate entry error
        rootCause?.message?.let {
            val matcher = duplicateEntryPattern.matcher(it)
            if (matcher.find()) {
                userFriendlyMessage = "The value '${matcher.group(1)}' already exists and must be unique."
            }
        }

        val path = (request as ServletWebRequest).request.requestURI

        logger.warn("Data Integrity Violation for path '{}': {}", path, userFriendlyMessage, ex)

        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.CONFLICT.value(),
            error = "Data Integrity Violation",
            message = userFriendlyMessage,
            path = path
        )
        return ResponseEntity(errorDetails, HttpStatus.CONFLICT)
    }

    /**
     * A fallback handler for any other unhandled exceptions.
     * Returns a 500 Internal Server Error to prevent exposing stack traces.
     */
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val path = (request as ServletWebRequest).request.requestURI

        logger.error("Unhandled exception for path '{}'", path, ex)

        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error has occurred. Please contact support.",
            path = path
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}