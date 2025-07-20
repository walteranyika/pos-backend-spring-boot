package com.walter.pos.repository

import com.walter.pos.entities.HeldOrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface HeldOrderItemRepository : JpaRepository<HeldOrderItem, Long>