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
    fun getUnits(@RequestParam(name = "q", required = false) query: String?): List<ProductUnitResponse> {
        return if (query.isNullOrBlank()) {
            unitService.getAllUnits()
        } else {
            unitService.searchUnits(query)
        }
    }
    @GetMapping("/{id}")
    fun getUnitById(@PathVariable id: Long): ProductUnitResponse = unitService.getUnitById(id)

    @PostMapping
    fun createUnit(@RequestBody request: ProductUnitRequest): ResponseEntity<ProductUnitResponse> {
        val unit = unitService.createUnit(request)
        return ResponseEntity(unit, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateUnit(@PathVariable id: Long, @RequestBody request: ProductUnitRequest): ProductUnitResponse =
        unitService.updateUnit(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUnit(@PathVariable id: Long) = unitService.deleteUnit(id)
}