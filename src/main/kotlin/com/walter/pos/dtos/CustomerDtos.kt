package com.walter.pos.dtos

import jakarta.validation.constraints.NotBlank

data class CustomerResponse(
    val id: Long,
    val name: String,
    val phoneNumber: String?
)

data class CreateCustomerRequest(
    @field:NotBlank(message = "Customer name cannot be blank")
    val name: String,
    val phoneNumber: String?
)