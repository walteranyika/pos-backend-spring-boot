package com.walter.pos.service


import com.walter.pos.dtos.BulkImportResponse
import com.walter.pos.dtos.CategoryResponse
import com.walter.pos.dtos.ProductRequest
import com.walter.pos.dtos.ProductResponse
import com.walter.pos.dtos.ProductUnitResponse
import com.walter.pos.dtos.ReorderItemResponse
import com.walter.pos.dtos.TaxType
import com.walter.pos.entities.Category
import com.walter.pos.entities.Product
import com.walter.pos.entities.ProductUnit
import com.walter.pos.entities.Stock
import com.walter.pos.exceptions.ResourceNotFoundException
import com.walter.pos.mappers.toResponse
import com.walter.pos.repository.CategoryRepository
import com.walter.pos.repository.ProductRepository
import com.walter.pos.repository.ProductUnitRepository
import com.walter.pos.repository.StockRepository
import jakarta.persistence.EntityNotFoundException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryService: CategoryService, // Reuse other services to find entities
    private val unitService: ProductUnitService,
    private val stockService: StockService,
    private val stockRepository: StockRepository,
    private val categoryRepository: CategoryRepository,
    private val unitRepository: ProductUnitRepository
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

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
        //stockService.createInitialStockForProduct(product)
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

    fun importProductsFromCsv(file: MultipartFile): BulkImportResponse {
        val errors = mutableListOf<String>()
        var successfulImports = 0
        var totalRecords = 0

        try {
            BufferedReader(InputStreamReader(file.inputStream)).use { reader ->
                val csvParser = CSVParser(
                    reader,
                    CSVFormat.DEFAULT.builder()
                        .setHeader() // Use the first row as header
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .build()
                )

                for (csvRecord in csvParser) {
                    totalRecords++
                    try {
                        // Process each record in its own transaction
                        createProductFromCsvRecord(csvRecord)
                        successfulImports++
                    } catch (e: Exception) {
                        val errorMessage = "Error on line ${csvRecord.recordNumber + 1}: ${e.message}"
                        logger.error(errorMessage, e)
                        errors.add(errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            val fatalError = "A fatal error occurred during file processing: ${e.message}"
            logger.error(fatalError, e)
            errors.add(fatalError)
        }

        return BulkImportResponse(
            totalRecords = totalRecords,
            successfulImports = successfulImports,
            failedImports = errors.size,
            errors = errors
        )
    }

    /**
     * Creates a single Product and its initial Stock from a CSV record.
     * This method is transactional, ensuring that either both the Product and its Stock
     * are created, or neither is.
     */
    @Transactional
    fun createProductFromCsvRecord(record: org.apache.commons.csv.CSVRecord) {
        val code = record.get("code")
        if (productRepository.findByCode(code).isPresent) {
            throw IllegalStateException("Product with code '$code' already exists.")
        }

        // --- Look up related entities ---
        val categoryName = record.get("categoryName")
        val category = categoryRepository.findByName(categoryName)
            .orElseThrow { EntityNotFoundException("Category '$categoryName' not found.") }

        val saleUnitAbbr = record.get("saleUnitAbbr")
        val saleUnit = unitRepository.findByShortName(saleUnitAbbr)
            .orElseThrow { EntityNotFoundException("Sale Unit '$saleUnitAbbr' not found.") }

        val purchaseUnitAbbr = record.get("purchaseUnitAbbr")
        val purchaseUnit = unitRepository.findByShortName(purchaseUnitAbbr)
            .orElseThrow { EntityNotFoundException("Purchase Unit '$purchaseUnitAbbr' not found.") }

        // --- Create Product ---
        val product = Product(
            code = code,
            name = record.get("name"),
            barcode = record.get("barcode").ifEmpty { null },
            cost = BigDecimal(record.get("cost")),
            price = BigDecimal(record.get("price")),
            isVariablePriced = record.get("isVariablePriced").toBooleanStrictOrNull() ?: false,
            saleUnit = saleUnit,
            purchaseUnit = purchaseUnit,
            category = category,
            stockAlert = BigDecimal(record.get("stockAlert")),
            taxMethod = TaxType.valueOf(record.get("taxMethod").uppercase()),
            isActive = record.get("isActive").toBooleanStrictOrNull() ?: true,
            note = record.get("note").ifEmpty { null }
        )
        val savedProduct = productRepository.save(product)

        // --- Create Initial Stock ---
        val initialStockQty = BigDecimal(record.get("initialStock"))
        val stock = Stock(
            product = savedProduct,
            quantity = initialStockQty
        )
        stockRepository.save(stock)
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