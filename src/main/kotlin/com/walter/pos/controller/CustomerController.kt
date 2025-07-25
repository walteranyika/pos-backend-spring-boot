package com.walter.pos.controller

import com.walter.pos.dtos.CreateCustomerRequest
import com.walter.pos.service.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customers")
class CustomerController(private val customerService: CustomerService) {

    @GetMapping
    @PreAuthorize("hasAuthority('CREATE_SALE')")
    fun getAllCustomers(): ResponseEntity<Any> {
        return ResponseEntity.ok(customerService.getAllCustomers())
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_SALE')")
    fun createCustomer(@Valid @RequestBody request: CreateCustomerRequest): ResponseEntity<Any> {
        return try {
            val customer = customerService.createCustomer(request)
            ResponseEntity(customer, HttpStatus.CREATED)
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }
}