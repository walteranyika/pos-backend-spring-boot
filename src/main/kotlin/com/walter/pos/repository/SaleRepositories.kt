package com.walter.pos.repository

import com.walter.pos.dtos.ProductPopularityDto
import com.walter.pos.entities.PaymentSale
import com.walter.pos.entities.Sale
import com.walter.pos.entities.SaleItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SaleRepository : JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale>{
}

@Repository
interface SaleDetailRepository : JpaRepository<SaleItem, Long>{
    @Query("""
        SELECT new com.walter.pos.dtos.ProductPopularityDto(si.product.id, SUM(si.quantity * si.price))
        FROM SaleItem si
        WHERE si.sale.createdAt >= :since
        GROUP BY si.product.id
    """)
    fun getProductPopularityScores(@Param("since") since: LocalDateTime):  List<ProductPopularityDto>

}

@Repository
interface PaymentSaleRepository : JpaRepository<PaymentSale, Long>