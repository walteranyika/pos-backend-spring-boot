package com.walter.pos.repository

import com.walter.pos.entities.ProductUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductUnitRepository : JpaRepository<ProductUnit, Int> {
    @Query("SELECT u FROM ProductUnit u WHERE lower(u.name) LIKE lower(concat('%', :query, '%')) OR lower(u.shortName) LIKE lower(concat('%', :query, '%'))")
    fun search(@Param("query") query: String): List<ProductUnit>
}