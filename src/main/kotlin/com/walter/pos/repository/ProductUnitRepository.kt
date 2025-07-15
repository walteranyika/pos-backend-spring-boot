package com.walter.pos.repository

import com.walter.pos.entities.ProductUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductUnitRepository : JpaRepository<ProductUnit, Int>{
}