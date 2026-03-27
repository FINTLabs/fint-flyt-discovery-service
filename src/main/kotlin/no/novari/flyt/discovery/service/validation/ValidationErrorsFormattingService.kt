package no.novari.flyt.discovery.service.validation

import jakarta.validation.ConstraintViolation
import org.springframework.stereotype.Service

@Service
class ValidationErrorsFormattingService {
    fun <T> format(errors: Set<ConstraintViolation<T>>): String {
        val prefix = if (errors.size > 1) "Validation errors" else "Validation error"
        val formattedErrors =
            errors
                .map { constraintViolation ->
                    val propertyPath = constraintViolation.propertyPath.toString()
                    val field = if (propertyPath.isBlank()) "" else "$propertyPath "
                    "'$field${constraintViolation.message}'"
                }.sorted()
                .joinToString(prefix = "[", postfix = "]")

        return "$prefix: $formattedErrors"
    }
}
