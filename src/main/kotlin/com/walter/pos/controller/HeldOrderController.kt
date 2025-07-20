package com.walter.pos.controller


import com.walter.pos.dtos.CreateSaleRequest
import com.walter.pos.dtos.HeldOrderResponse
import com.walter.pos.dtos.HoldOrderRequest
import com.walter.pos.dtos.SaleResponse
import com.walter.pos.service.HeldOrderService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/held-orders")
class HeldOrderController(private val heldOrderService: HeldOrderService) {

    @PostMapping
    fun holdOrder(@Valid @RequestBody request: HoldOrderRequest): ResponseEntity<HeldOrderResponse> {
        return ResponseEntity.ok(heldOrderService.createHeldOrder(request))
    }

    @GetMapping
    fun getMyHeldOrders(): ResponseEntity<List<HeldOrderResponse>> {
        return ResponseEntity.ok(heldOrderService.getHeldOrdersForCurrentUser())
    }

    @PutMapping("/{id}")
    fun updateHeldOrder(
        @PathVariable id: Long,
        @Valid @RequestBody request: HoldOrderRequest
    ): ResponseEntity<HeldOrderResponse> {
        return ResponseEntity.ok(heldOrderService.updateHeldOrder(id, request))
    }

    @DeleteMapping("/{id}")
    fun deleteHeldOrder(@PathVariable id: Long): ResponseEntity<Unit> {
        heldOrderService.deleteHeldOrder(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/resume")
    fun resumeOrder(
        @PathVariable id: Long,
        @Valid @RequestBody paymentRequest: CreateSaleRequest
    ): ResponseEntity<SaleResponse> {
        return ResponseEntity.ok(heldOrderService.resumeAndCompleteSale(id, paymentRequest))
    }
}