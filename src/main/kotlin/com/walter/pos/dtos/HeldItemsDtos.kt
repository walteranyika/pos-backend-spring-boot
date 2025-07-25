package com.walter.pos.dtos

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class HoldOrderRequest(
    @field:NotEmpty
    val items: List<@Valid HoldOrderItemRequest>,
    val customerId: Long
)

data class HoldOrderItemRequest(
    @field:NotNull
    val productId: Long,
    @field:NotNull
    val quantity: BigDecimal,
)

// Response for a single held order
data class HeldOrderResponse(
    val id: Long,
    val customerId: Long,
    val ref: String,
    val items: List<HeldOrderItemResponse>,
    val createdAt: LocalDateTime?=null
)

data class HeldOrderItemResponse(
    val productId: Long,
    val productName: String,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val createdAt: LocalDateTime?=null
)