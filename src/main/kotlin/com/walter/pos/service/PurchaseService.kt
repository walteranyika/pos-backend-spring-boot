package com.walter.pos.service


import com.walter.pos.dtos.PurchaseItemResponse
import com.walter.pos.dtos.PurchaseRequest
import com.walter.pos.dtos.PurchaseResponse
import com.walter.pos.entities.Purchase
import com.walter.pos.entities.PurchaseItem
import com.walter.pos.repository.ProductRepository
import com.walter.pos.repository.PurchaseRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PurchaseService(
    private val purchaseRepository: PurchaseRepository,
    private val productRepository: ProductRepository,
    private val stockService: StockService
) {

    @Transactional
    fun createPurchase(request: PurchaseRequest): PurchaseResponse {
        val purchase = Purchase(
            ref = "PUR-${System.currentTimeMillis()}",
            supplier = request.supplier,
            totalCost = BigDecimal.ZERO // Will be calculated
        )

        val purchaseItems = request.items.map { itemRequest ->
            val product = productRepository.findById(itemRequest.productId)
                .orElseThrow { EntityNotFoundException("Product not found with ID: ${itemRequest.productId}") }

            // CRITICAL: Update stock levels
            stockService.addStock(product.id, itemRequest.quantity)

            PurchaseItem(
                product = product,
                purchase = purchase,
                quantity = itemRequest.quantity,
                costPrice = itemRequest.costPrice,
                totalCost = itemRequest.quantity.multiply(itemRequest.costPrice)
            )
        }

        purchase.items.addAll(purchaseItems)
        purchase.totalCost = purchaseItems.sumOf { it.totalCost }

        val savedPurchase = purchaseRepository.save(purchase)
        return savedPurchase.toResponse()
    }

    fun getAllPurchases(): List<PurchaseResponse> {
        return purchaseRepository.findAll().map { it.toResponse() }
    }

    // Using an extension function for clean mapping
    private fun Purchase.toResponse(): PurchaseResponse = PurchaseResponse(
        id = this.id,
        ref = this.ref,
        supplier = this.supplier,
        totalCost = this.totalCost,
        purchaseDate = this.purchaseDate,
        items = this.items.map {
            PurchaseItemResponse(
                productName = it.product.name,
                quantity = it.quantity,
                costPrice = it.costPrice,
                totalCost = it.totalCost
            )
        }
    )
}