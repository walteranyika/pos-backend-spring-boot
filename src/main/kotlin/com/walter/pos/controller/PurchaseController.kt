package com.walter.pos.controller

import com.walter.pos.dtos.PurchaseRequest
import com.walter.pos.dtos.PurchaseResponse
import com.walter.pos.service.PurchaseService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/purchases")
class PurchaseController(private val purchaseService: PurchaseService) {

    @PostMapping
    fun createPurchase(@Valid @RequestBody request: PurchaseRequest): ResponseEntity<PurchaseResponse> {
        return ResponseEntity.ok(purchaseService.createPurchase(request))
    }

    @GetMapping
    fun getAllPurchases(): ResponseEntity<List<PurchaseResponse>> {
        return ResponseEntity.ok(purchaseService.getAllPurchases())
    }
}