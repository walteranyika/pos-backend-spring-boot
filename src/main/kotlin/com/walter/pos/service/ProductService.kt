package com.walter.pos.service


import com.walter.pos.dtos.CategoryResponse
import com.walter.pos.dtos.ProductRequest
import com.walter.pos.dtos.ProductResponse
import com.walter.pos.dtos.ProductUnitResponse
import com.walter.pos.dtos.ReorderItemResponse
import com.walter.pos.entities.Category
import com.walter.pos.entities.Product
import com.walter.pos.entities.ProductUnit
import com.walter.pos.entities.Stock
import com.walter.pos.exceptions.ResourceNotFoundException
import com.walter.pos.mappers.toResponse
import com.walter.pos.repository.ProductRepository
import com.walter.pos.repository.StockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryService: CategoryService, // Reuse other services to find entities
    private val unitService: ProductUnitService,
    private val stockService: StockService,
    private val stockRepository: StockRepository
) {

    fun getAllProducts(): List<ProductResponse> {
        val products = productRepository.findAll()
        if (products.isEmpty()) {
            return emptyList()
        }

        val productIds = products.map { it.id }

        val stocks = stockService.getStocksForProducts(productIds)

        return products.map { it.toResponse(stocks[it.id]?.quantity ?: BigDecimal.ZERO) }
    }

    fun getProductById(id: Long): ProductResponse = findProductById(id).toResponse()

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
        val savedProduct = productRepository.save(product)
        stockService.createInitialStockForProduct(product)
        // 2. Automatically create the corresponding stock record
        val initialStock = Stock(
            product = savedProduct,
            quantity = BigDecimal.ZERO // Initialize with quantity 0
        )
        stockRepository.save(initialStock)
        return savedProduct.toResponse()
    }

    @Transactional
    fun updateProduct(id: Long, request: ProductRequest): ProductResponse {
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

    fun deleteProduct(id: Long) {
        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException("Product with ID $id not found.")
        }
        productRepository.deleteById(id)
    }

    private fun findProductById(id: Long): Product =
        productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product with ID $id not found.") }


    fun searchProducts(query: String?, categoryId: Long?): List<ProductResponse> {
       val products =  productRepository.searchAndFilter(query, categoryId)
        if (products.isEmpty()) {
            return emptyList()
        }

        val productIds = products.map { it.id }

        val stocks = stockService.getStocksForProducts(productIds)

        return products.map { it.toResponse(stocks[it.id]?.quantity ?: BigDecimal.ZERO) }
    }

    fun getReorderAlertItems(): List<ReorderItemResponse> {
        val reorderItems = productRepository.findProductsForReOrder()
        return reorderItems
    }
}