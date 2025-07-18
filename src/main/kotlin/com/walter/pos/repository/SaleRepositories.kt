package com.walter.pos.repository

import com.walter.pos.entities.PaymentSale
import com.walter.pos.entities.Sale
import com.walter.pos.entities.SaleItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SaleRepository : JpaRepository<Sale, Int>, JpaSpecificationExecutor<Sale>

@Repository
interface SaleDetailRepository : JpaRepository<SaleItem, Int>

@Repository
interface PaymentSaleRepository : JpaRepository<PaymentSale, Int>