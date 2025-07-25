package com.walter.pos.repository

import com.walter.pos.entities.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE lower(c.name) LIKE lower(concat('%', :query, '%')) OR lower(c.code) LIKE lower(concat('%', :query, '%'))")
    fun search(@Param("query") query: String): List<Category>

    fun findByName(name: String): Optional<Category>
}