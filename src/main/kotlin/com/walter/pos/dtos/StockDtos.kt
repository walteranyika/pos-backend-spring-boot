package com.walter.pos.dtos

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class StockAdjustmentRequest(
    @field:NotNull val productId: Long,
    @field:NotNull val newQuantity: BigDecimal
)