package com.walter.pos.dtos

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class StockAdjustmentRequest(
    @field:NotNull val productId: Long,
    @field:NotNull val newQuantity: BigDecimal
)





/**
 * A read-only DTO representing a product that needs to be re-ordered.
 */
data class ReorderItemResponse(
    val productId: Long,
    val productCode: String,
    val productName: String,
    val currentQuantity: BigDecimal,
    val stockAlertLevel: BigDecimal,
    val saleUnitName: String
)