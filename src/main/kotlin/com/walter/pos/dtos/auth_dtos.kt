package com.walter.pos.dtos

data class LoginRequest(
    val username: String,
    val pin: String
)

data class LoginResponse(
    val token: String,
    val fullName: String,
    val username: String,
    val permissions: Set<String>
)

data class ResetPinRequest(
    val newPin: String
)


data class UpdateUserRequest(
    val username: String,
    val fullName: String
)
