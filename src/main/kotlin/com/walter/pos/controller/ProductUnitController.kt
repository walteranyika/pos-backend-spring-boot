package com.walter.pos.controller

import com.walter.pos.dtos.ProductUnitResponse
import com.walter.pos.dtos.ProductUnitRequest
import com.walter.pos.service.ProductUnitService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/units")
class ProductUnitController(private val unitService: ProductUnitService) {

    @GetMapping
    fun getAllUnits(): List<ProductUnitResponse> = unitService.getAllUnits()

    @GetMapping("/{id}")
    fun getUnitById(@PathVariable id: Int): ProductUnitResponse = unitService.getUnitById(id)

    @PostMapping
    fun createUnit(@RequestBody request: ProductUnitRequest): ResponseEntity<ProductUnitResponse> {
        val unit = unitService.createUnit(request)
        return ResponseEntity(unit, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateUnit(@PathVariable id: Int, @RequestBody request: ProductUnitRequest): ProductUnitResponse =
        unitService.updateUnit(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUnit(@PathVariable id: Int) = unitService.deleteUnit(id)
}