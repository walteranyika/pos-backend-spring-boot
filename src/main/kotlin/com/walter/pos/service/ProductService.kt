package com.walter.pos.service


import com.walter.pos.dtos.CategoryResponse
import com.walter.pos.dtos.ProductRequest
import com.walter.pos.dtos.ProductResponse
import com.walter.pos.dtos.ProductUnitResponse
import com.walter.pos.entities.Category
import com.walter.pos.entities.Product
import com.walter.pos.entities.ProductUnit
import com.walter.pos.exceptions.ResourceNotFoundException
import com.walter.pos.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryService: CategoryService, // Reuse other services to find entities
    private val unitService: ProductUnitService
) {

    fun getAllProducts(): List<ProductResponse> = productRepository.findAll().map { it.toResponse() }

    fun getProductById(id: Int): ProductResponse = findProductById(id).toResponse()

    @Transactional
    fun createProduct(request: ProductRequest): ProductResponse {
        // Find the related entities using their respective services
        val category = categoryService.findCategoryById(request.categoryId)
        val saleUnit = unitService.findUnitById(request.saleUnitId)
        val purchaseUnit = unitService.findUnitById(request.purchaseUnitId)

        val product = Product(
            code = request.code,
            name = request.name,
            barcode = request.barcode,
            cost = request.cost,
            price = request.price,
            isVariablePriced = request.isVariablePriced,
            saleUnit = saleUnit,
            purchaseUnit = purchaseUnit,
            stockAlert = request.stockAlert,
            category = category,
            taxMethod = request.taxMethod,
            image = request.image,
            isActive = request.isActive,
            note = request.note
        )
        return productRepository.save(product).toResponse()
    }

    @Transactional
    fun updateProduct(id: Int, request: ProductRequest): ProductResponse {
        val product = findProductById(id)
        val category = categoryService.findCategoryById(request.categoryId)
        val saleUnit = unitService.findUnitById(request.saleUnitId)
        val purchaseUnit = unitService.findUnitById(request.purchaseUnitId)

        // Update the product fields
        product.apply {
            this.code = request.code
            this.barcode = request.barcode
            this.cost = request.cost
            this.price = request.price
            this.isVariablePriced = request.isVariablePriced
            this.saleUnit = saleUnit
            this.purchaseUnit = purchaseUnit
            this.stockAlert = request.stockAlert
            this.category = category
            this.taxMethod = request.taxMethod
            this.image = request.image
            this.isActive = request.isActive
            this.note = request.note
        }

        return productRepository.save(product).toResponse()
    }

    fun deleteProduct(id: Int) {
        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException("Product with ID $id not found.")
        }
        productRepository.deleteById(id)
    }

    private fun findProductById(id: Int): Product =
        productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product with ID $id not found.") }

    // Mapper extension function for Product
    private fun Product.toResponse() = ProductResponse(
        id = this.id,
        code = this.code,
        name = this.name,
        barcode = this.barcode,
        cost = this.cost,
        price = this.price,
        isVariablePriced = this.isVariablePriced,
        saleUnit = this.saleUnit.toResponse(),
        purchaseUnit = this.purchaseUnit.toResponse(),
        stockAlert = this.stockAlert,
        category = this.category.toResponse(),
        taxMethod = this.taxMethod,
        image = this.image,
        isActive = this.isActive,
        note = this.note,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
    // We need mappers for the nested objects as well
    private fun ProductUnit.toResponse() = ProductUnitResponse(this.id, this.name, this.shortName)
    private fun Category.toResponse() = CategoryResponse(this.id, this.name, this.code)
}