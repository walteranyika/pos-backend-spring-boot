package com.walter.pos.dtos;


import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.OffsetDateTime

data class PurchaseRequest(
        val supplier: String?,
        @field:NotEmpty val items: List<@Valid PurchaseItemRequest>
)

data class PurchaseItemRequest(
        @field:NotNull val productId: Long,
        @field:NotNull val quantity: BigDecimal,
        @field:NotNull val costPrice: BigDecimal
)

data class PurchaseResponse(
        val id: Long,
        val ref: String,
        val supplier: String?,
        val totalCost: BigDecimal,
        val purchaseDate: OffsetDateTime?,
        val items: List<PurchaseItemResponse>
)

data class PurchaseItemResponse(
        val productName: String,
        val quantity: BigDecimal,
        val costPrice: BigDecimal,
        val totalCost: BigDecimal
)