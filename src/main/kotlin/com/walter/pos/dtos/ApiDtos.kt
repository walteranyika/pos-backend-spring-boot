package com.walter.pos.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

// --- Category DTOs ---
data class CategoryResponse(
    val id: Long,
    val name: String,
    val code: String,
    val createdAt: LocalDateTime?=null,
    val updatedAt: LocalDateTime?=null
)

data class CategoryRequest(
    val name: String,
    val code: String
)

// --- Unit DTOs ---
data class ProductUnitResponse(
    val id: Long,
    val name: String,
    val shortName: String,
    val createdAt: LocalDateTime?=null,
    val updatedAt: LocalDateTime?=null
)

data class ProductUnitRequest(
    val name: String,
    val shortName: String
)

// --- Product DTOs ---
data class ProductResponse(
    val id: Long,
    val code: String,
    val name: String,
    val barcode: String?,
    val cost: BigDecimal,
    val price: BigDecimal,
    val isVariablePriced: Boolean,
    val saleUnit: ProductUnitResponse,
    val purchaseUnit: ProductUnitResponse,
    val stockAlert: BigDecimal,
    val quantity: BigDecimal,
    val category: CategoryResponse,
    val taxMethod: TaxType,
    val image: String?,
    val isActive: Boolean,
    val note: String?,
    val createdAt: LocalDateTime?=null,
    val updatedAt: LocalDateTime?=null
)

data class ProductRequest(
    val code: String,
    val name: String,
    val barcode: String?,
    val cost: BigDecimal,
    val price: BigDecimal,
    val isVariablePriced: Boolean,
    val saleUnitId: Long,
    val purchaseUnitId: Long,
    val stockAlert: BigDecimal,
    val categoryId: Long,
    val taxMethod: TaxType,
    val isActive: Boolean,
    val note: String?
)