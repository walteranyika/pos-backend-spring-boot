package com.walter.pos.service

import com.walter.pos.dtos.*
import com.walter.pos.entities.*
import com.walter.pos.exceptions.ResourceNotFoundException
import com.walter.pos.repository.PaymentSaleRepository
import com.walter.pos.repository.ProductRepository
import com.walter.pos.repository.SaleDetailRepository
import com.walter.pos.repository.SaleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PosService(
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository,
    private val saleDetailRepository: SaleDetailRepository,
    private val paymentSaleRepository: PaymentSaleRepository,
    private val stockService: StockService
) {

    @Transactional
    fun createSale(request: CreateSaleRequest, user: User): SaleResponse {
        val ref = "SALE-${System.currentTimeMillis()}"

        // 1. Fetch all products at once to validate IDs and improve performance
        val productIds = request.items.map { it.productId }
        val products = productRepository.findAllById(productIds)
        if (products.size != productIds.distinct().size) {
            val foundIds = products.map { it.id }.toSet()
            val notFoundIds = productIds.filterNot { foundIds.contains(it) }
            throw ResourceNotFoundException("Products not found with IDs: $notFoundIds")
        }
        val productMap = products.associateBy { it.id }

        // 2. Calculate totals and payment status
        val totalFromItems = request.items.sumOf { (it.price * it.quantity) - it.discount }
        val grandTotal = totalFromItems - request.discount
        val paidAmount = request.payments.sumOf { it.amount }

        val paymentStatus = when {
            paidAmount >= grandTotal -> PaymentStatus.PAID
            paidAmount > BigDecimal.ZERO -> PaymentStatus.PARTIAL
            else -> PaymentStatus.UNPAID
        }

        // 3. Create and save the main Sale entity
        val sale = Sale(
            ref = ref,
            user = user,
            grandTotal = grandTotal,
            discount = request.discount,
            paidAmount = paidAmount,
            paymentStatus = paymentStatus,
            isCreditSale = request.isCreditSale
        )
        val savedSale = saleRepository.save(sale)

        // 4. Create SaleDetail entities linked to the saved Sale
        val saleDetailsToSave = request.items.map { itemRequest ->
            val product = productMap[itemRequest.productId]
                ?: throw IllegalStateException("Product mapping failed, this should not happen.")
            SaleItem(
                sale = savedSale,
                product = product,
                quantity = itemRequest.quantity,
                price = itemRequest.price,
                discount = itemRequest.discount,
                total = (itemRequest.price * itemRequest.quantity) - itemRequest.discount
            )
        }
        val savedSaleDetails = saleDetailRepository.saveAll(saleDetailsToSave)


        // 5. Create PaymentSale entities if any payment was made
        val savedPayments = if (paidAmount > BigDecimal.ZERO) {
            val paymentsToSave = request.payments.map {
                PaymentSale(
                    sale = savedSale,
                    user = user,
                    amount = it.amount,
                    method = it.method,
                    notes = it.notes
                )
            }
            paymentSaleRepository.saveAll(paymentsToSave)
        } else {
            emptyList()
        }

        //update product stock levels here.
        savedSaleDetails.forEach {
                item -> stockService.reduceStock(item.product.id, item.quantity)
        }

        // 6. Construct and return the response DTO
        return savedSale.toResponse(savedSaleDetails, savedPayments)
    }

    // Mapper extension functions to convert entities to DTOs
    private fun Sale.toResponse(details: List<SaleItem>, payments: List<PaymentSale>) = SaleResponse(
        id = this.id,
        ref = this.ref,
        grandTotal = this.grandTotal,
        discount = this.discount,
        paidAmount = this.paidAmount,
        paymentStatus = this.paymentStatus,
        isCreditSale = this.isCreditSale,
        cashier = "${this.user.firstName} ${this.user.lastName}",
        saleDate = this.createdAt,
        items = details.map { it.toResponse() },
        payments = payments.map { it.toResponse() }
    )

    private fun SaleItem.toResponse() = SaleDetailResponse(
        productName = this.product.name,
        quantity = this.quantity,
        price = this.price,
        discount = this.discount,
        total = this.total
    )

    private fun PaymentSale.toResponse() = PaymentSaleResponse(
        amount = this.amount,
        method = this.method,
        paidAt = this.createdAt
    )
}