package com.walter.pos.repository

import com.walter.pos.dtos.ReorderItemResponse
import com.walter.pos.entities.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>{
    @Query("""
        SELECT p FROM Product p WHERE
        (:query IS NULL OR lower(p.name) LIKE lower(concat('%', :query, '%')) OR lower(p.code) LIKE lower(concat('%', :query, '%')))
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
    """)
    fun searchAndFilter(@Param("query") query: String?, @Param("categoryId") categoryId: Long?): List<Product>



    @Query("""
        SELECT new com.walter.pos.dtos.ReorderItemResponse(
         p.id,
         p.code,
         p.name,
         s.quantity,
         p.stockAlert,
         p.saleUnit.name
        )
        FROM Product p JOIN Stock s ON p.id= s.product.id
        WHERE p.isActive = true AND s.quantity<= p.stockAlert 
        ORDER BY p.name ASC
      """)
    fun findProductsForReOrder(): List<ReorderItemResponse>

}