package com.walter.pos.repository

import com.walter.pos.entities.HeldOrder
import org.springframework.data.jpa.repository.JpaRepository

interface HeldOrderRepository: JpaRepository<HeldOrder, Long> {
    fun findByUserId(userId: Long) : List<HeldOrder>
}