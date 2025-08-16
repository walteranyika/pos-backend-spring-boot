package com.walter.pos.service

import com.walter.pos.repository.ProductRepository
import com.walter.pos.repository.SaleDetailRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class ProductPopularityService(
    private val saleItemRepository: SaleDetailRepository,
    private val productRepository: ProductRepository
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    /**
     * A scheduled task that runs every day at midnight to update the popularity
     * score for all products based on sales from the last 30 days.
     */
    @Transactional
    //@Scheduled(cron = "0 0 0 * * *") // Runs every day at midnight
    @Scheduled(cron = "0 */30 * * * ?")
    fun updateProductPopularity() {
        logger.info ("Starting daily product popularity update task." )
        val thirtyDaysAgo = LocalDateTime.now().minusDays(30)

        // 1. Get popularity scores for products sold in the last 30 days
        val popularityScoresMap = saleItemRepository.getProductPopularityScores(thirtyDaysAgo)
            .associateBy { it.productId }

        // 2. Get all products from the database
        val allProducts = productRepository.findAll()

        var updatedCount = 0
        // 3. Update the popularity score for each product
        allProducts.forEach { product ->
            val newScore = popularityScoresMap[product.id]?.score ?: BigDecimal.ZERO
            // Only update if the score has changed to avoid unnecessary DB writes
            if (product.popularity.compareTo(newScore) != 0) {
                product.popularity = newScore
                updatedCount++
            }
        }

        // 4. Save all updated products in a single batch operation
        if (updatedCount > 0) {
            productRepository.saveAll(allProducts)
        }

        logger.info ("Finished product popularity update task. Updated $updatedCount products." )
    }
}