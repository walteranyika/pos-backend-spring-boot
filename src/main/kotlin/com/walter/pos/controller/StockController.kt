package com.walter.pos.controller

import com.walter.pos.dtos.StockAdjustmentRequest
import com.walter.pos.service.StockService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stock")
class StockController(private val stockService: StockService) {

    @PostMapping("/adjust")
    fun adjustStock(@Valid @RequestBody request: StockAdjustmentRequest): ResponseEntity<Unit> {
        stockService.adjustStock(request.productId, request.newQuantity)
        return ResponseEntity.ok().build()
    }
}
