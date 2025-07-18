package com.walter.pos.mappers

import com.walter.pos.dtos.CategoryResponse
import com.walter.pos.dtos.PaymentSaleResponse
import com.walter.pos.dtos.ProductUnitResponse
import com.walter.pos.dtos.SaleDetailResponse
import com.walter.pos.dtos.SaleResponse
import com.walter.pos.entities.Category
import com.walter.pos.entities.PaymentSale
import com.walter.pos.entities.ProductUnit
import com.walter.pos.entities.Sale
import com.walter.pos.entities.SaleItem

fun Category.toResponse() = CategoryResponse(
    id = this.id,
    name = this.name,
    code = this.code,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun ProductUnit.toResponse() = ProductUnitResponse(
    id = this.id,
    name = this.name,
    shortName = this.shortName
)

fun PaymentSale.toResponse() = PaymentSaleResponse(
    amount = this.amount,
    method = this.method,
    paidAt = this.createdAt
)

fun SaleItem.toResponse() = SaleDetailResponse(
    productName = this.product.name,
    quantity = this.quantity,
    price = this.price,
    discount = this.discount,
    total = this.total,
)

fun Sale.toResponse() = SaleResponse(
    id = this.id,
    ref = this.ref,
    grandTotal=this.grandTotal,
    discount=this.discount, // Overall sale discount
    paidAmount=this.paidAmount,
    paymentStatus=this.paymentStatus,
    isCreditSale=this.isCreditSale,
    cashier=this.user.username,
    saleDate=this.createdAt,
    items = this.details.map { it.toResponse() },
    payments= this.payments.map {  it.toResponse() },
    createdAt = this.createdAt
)