package com.walter.pos.dtos

import com.walter.pos.entities.Customer
import java.math.BigDecimal
import java.time.LocalDateTime

// --- Request DTOs ---

data class SaleItemRequest(
    val productId: Long,
    val quantity: BigDecimal,
    val price: BigDecimal, // The unit price at the time of sale
    val discount: BigDecimal = BigDecimal.ZERO // Item-specific discount
)

data class PaymentRequest(
    val amount: BigDecimal,
    val method: PaymentMethod,
    val notes: String? = null
)

data class CreateSaleRequest(
    val items: List<SaleItemRequest>,
    val payments: List<PaymentRequest>,
    val discount: BigDecimal = BigDecimal.ZERO, // An overall discount on the entire sale
    val isCreditSale: Boolean = false,
    val customerId: Long
)

// --- Response DTOs ---

data class SaleDetailResponse(
    val productName: String,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val discount: BigDecimal,
    val total: BigDecimal
)

data class PaymentSaleResponse(
    val amount: BigDecimal,
    val method: PaymentMethod,
    val paidAt: LocalDateTime?
)

data class SaleResponse(
    val id: Long,
    val ref: String,
    val grandTotal: BigDecimal,
    val discount: BigDecimal, // Overall sale discount
    val paidAmount: BigDecimal,
    val paymentStatus: PaymentStatus,
    val isCreditSale: Boolean,
    val cashier: String,
    val customer: CustomerResponse,
    val saleDate: LocalDateTime?,
    val items: List<SaleDetailResponse>,
    val payments: List<PaymentSaleResponse>,
    val createdAt: LocalDateTime? = null
)