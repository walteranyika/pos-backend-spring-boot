package com.walter.pos.service

import com.walter.pos.entities.Product
import com.walter.pos.entities.Stock
import com.walter.pos.repository.ProductRepository
import com.walter.pos.repository.StockRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val productRepository: ProductRepository
) {

    @Transactional
    fun createInitialStockForProduct(product: Product) {
        if (stockRepository.findByProductId(product.id).isEmpty) {
            stockRepository.save(Stock(product = product))
        }
    }

    @Transactional
    fun addStock(productId: Long, quantityToAdd: BigDecimal) {
        val stock = getStockByProductId(productId)
        stock.quantity = stock.quantity.add(quantityToAdd)
        stockRepository.save(stock)
    }

    @Transactional
    fun reduceStock(productId: Long, quantityToReduce: BigDecimal) {
        val stock = getStockByProductId(productId)
        var newQuantity = stock.quantity.subtract(quantityToReduce)
        if (newQuantity < BigDecimal.ZERO) {
            // In a real-world scenario, you might want to prevent sales of out-of-stock items earlier.
            // For now, we'll log a warning and set stock to zero.
            // throw IllegalStateException("Not enough stock for product ID: $productId")
            newQuantity = BigDecimal.ZERO
        }
        stock.quantity = newQuantity
        stockRepository.save(stock)
    }

    @Transactional
    fun adjustStock(productId: Long, newQuantity: BigDecimal) {
        val stock = getStockByProductId(productId)
        stock.quantity = newQuantity
        stockRepository.save(stock)
    }

    fun getStockByProductId(productId: Long): Stock {
        return stockRepository.findByProductId(productId)
            .orElseThrow { EntityNotFoundException("Stock not found for product ID: $productId") }
    }

    fun getStocksForProducts(productIds: List<Long>): Map<Long, Stock>{
        return stockRepository.findByProductIdIn(productIds).associateBy { it.product.id }
    }
}