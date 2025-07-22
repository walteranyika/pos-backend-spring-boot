package com.walter.pos.controller

import com.walter.pos.dtos.ProductRequest
import com.walter.pos.dtos.ProductResponse
import com.walter.pos.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getProducts(
        @RequestParam(name = "q", required = false) query: String?,
        @RequestParam(name = "categoryId", required = false) categoryId: Long?
    ): List<ProductResponse> {
        return productService.searchProducts(query?.trim(), categoryId)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ProductResponse = productService.getProductById(id)

    @PostMapping
    fun createProduct(@RequestBody request: ProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(request)
        return ResponseEntity(product, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody request: ProductRequest): ProductResponse =
        productService.updateProduct(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(@PathVariable id: Long) = productService.deleteProduct(id)

    /**
     * Imports products in bulk from a CSV file.
     * The file must be sent as multipart/form-data with the key "file".
     *
     * Expected CSV Headers:
     * code,name,barcode,cost,price,isVariablePriced,saleUnitAbbr,purchaseUnitAbbr,categoryName,stockAlert,initialStock,taxMethod,isActive,note
     */
    @PostMapping("/import/bulk")
    @PreAuthorize("hasAuthority('MANAGE_PRODUCTS')") // Secure the endpoint
    fun bulkImportProducts(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body("Please upload a CSV file.")
        }
        // You might want to add more validation for file type (e.g., text/csv)

        val result = productService.importProductsFromCsv(file)

        return if (result.failedImports > 0) {
            // Return a 422 Unprocessable Entity if there were errors
            ResponseEntity.unprocessableEntity().body(result)
        } else {
            ResponseEntity.ok(result)
        }
    }

}