package com.walter.pos.controller

import com.walter.pos.dtos.CreateSaleRequest
import com.walter.pos.dtos.SaleResponse
import com.walter.pos.entities.User
import com.walter.pos.service.PosService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sales")
class PosController(private val posService: PosService) {

    @PostMapping
    fun createSale(
        @RequestBody request: CreateSaleRequest,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<SaleResponse> {
        val saleResponse = posService.createSale(request, user)
        return ResponseEntity.status(HttpStatus.CREATED).body(saleResponse)
    }
}