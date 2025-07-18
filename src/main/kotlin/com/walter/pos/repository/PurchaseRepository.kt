package com.walter.pos.repository


import com.walter.pos.entities.Purchase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface PurchaseRepository : JpaRepository<Purchase, Long>