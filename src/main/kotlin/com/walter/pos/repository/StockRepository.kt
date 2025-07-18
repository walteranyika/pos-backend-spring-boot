package com.walter.pos.repository



import com.walter.pos.entities.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StockRepository : JpaRepository<Stock, Long> {
    fun findByProductId(productId: Long): Optional<Stock>
    fun findByProductIdIn(productIds: List<Long>): List<Stock>
}
