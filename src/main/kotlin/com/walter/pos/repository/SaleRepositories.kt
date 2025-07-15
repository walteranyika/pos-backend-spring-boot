package com.walter.pos.repository

import com.walter.pos.entities.PaymentSale
import com.walter.pos.entities.Sale
import com.walter.pos.entities.SaleDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaleRepository : JpaRepository<Sale, Int>

@Repository
interface SaleDetailRepository : JpaRepository<SaleDetail, Int>

@Repository
interface PaymentSaleRepository : JpaRepository<PaymentSale, Int>