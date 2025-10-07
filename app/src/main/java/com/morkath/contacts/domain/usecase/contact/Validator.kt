package com.morkath.contacts.domain.usecase.contact

import javax.inject.Inject

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

class ValidatorNameUseCase @Inject constructor() {
    operator fun invoke(name: String): ValidationResult {
        val regex = Regex("^[\\p{L}0-9 .'-]+$")
        return if (name.isBlank()) {
            ValidationResult(
                isValid = false,
                errorMessage = "Name cannot be empty"
            )
        } else if (!name.matches(regex)) {
            ValidationResult(
                isValid = false,
                errorMessage = "Name contains invalid characters"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }
}

class ValidatorPhoneUseCase @Inject constructor() {
    operator fun invoke(phone: String): ValidationResult {
        val regex = Regex("^\\+?[0-9 ()-]{10,20}\$")
        return if (phone.isBlank()) {
            ValidationResult(
                isValid = false,
                errorMessage = "Phone number cannot be empty"
            )
        } else if (!phone.matches(regex)) {
            ValidationResult(
                isValid = false,
                errorMessage = "Invalid phone number format"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }
}

class ValidatorEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ValidationResult {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return if (email.isBlank()) {
            ValidationResult(
                isValid = false,
                errorMessage = "Email cannot be empty"
            )
        } else if (!email.matches(regex)) {
            ValidationResult(
                isValid = false,
                errorMessage = "Invalid email format"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }
}