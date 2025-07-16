package com.walter.pos.controller

import com.walter.pos.dtos.ProductRequest
import com.walter.pos.dtos.ProductResponse
import com.walter.pos.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getProducts(
        @RequestParam(name = "q", required = false) query: String?,
        @RequestParam(name = "categoryId", required = false) categoryId: Int?
    ): List<ProductResponse> {
        val isSearchingOrFiltering = !query.isNullOrBlank() || categoryId != null
        return if (isSearchingOrFiltering) {
            productService.searchProducts(query?.trim(), categoryId)
        } else productService.getAllProducts()
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Int): ProductResponse = productService.getProductById(id)

    @PostMapping
    fun createProduct(@RequestBody request: ProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(request)
        return ResponseEntity(product, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Int, @RequestBody request: ProductRequest): ProductResponse =
        productService.updateProduct(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(@PathVariable id: Int) = productService.deleteProduct(id)
}