package com.walter.pos.mappers

import com.walter.pos.dtos.CategoryResponse
import com.walter.pos.dtos.CustomerResponse
import com.walter.pos.dtos.HeldOrderItemResponse
import com.walter.pos.dtos.HeldOrderResponse
import com.walter.pos.dtos.PaymentSaleResponse
import com.walter.pos.dtos.ProductResponse
import com.walter.pos.dtos.ProductUnitResponse
import com.walter.pos.dtos.SaleDetailResponse
import com.walter.pos.dtos.SaleResponse
import com.walter.pos.entities.Category
import com.walter.pos.entities.Customer
import com.walter.pos.entities.HeldOrder
import com.walter.pos.entities.PaymentSale
import com.walter.pos.entities.Product
import com.walter.pos.entities.ProductUnit
import com.walter.pos.entities.Sale
import com.walter.pos.entities.SaleItem
import com.walter.pos.utils.DateUtils
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    customer=this.customer.toResponse(),
    saleDate=this.createdAt,
    items = this.details.map { it.toResponse() },
    payments= this.payments.map {  it.toResponse() },
    createdAt = this.createdAt
)

fun Product.toResponse(quantity: BigDecimal= BigDecimal.ZERO) = ProductResponse(
    id = this.id,
    code = this.code,
    name = this.name,
    barcode = this.barcode,
    cost = this.cost,
    price = this.price,
    isVariablePriced = this.isVariablePriced,
    saleUnit = this.saleUnit.toResponse(),
    purchaseUnit = this.purchaseUnit.toResponse(),
    stockAlert = this.stockAlert,
    popularity = this.popularity,
    category = this.category.toResponse(),
    taxMethod = this.taxMethod,
    image = this.image,
    isActive = this.isActive,
    note = this.note,
    quantity = quantity,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun Customer.toResponse(): CustomerResponse {
    return CustomerResponse(
        id = this.id,
        name = this.name,
        phoneNumber = this.phoneNumber
    )
}

fun HeldOrder.toResponse(): HeldOrderResponse = HeldOrderResponse(
    id = this.id,
    ref = this.ref,
    customerId = this.customer.id,
    customerName = this.customer.name,
    items = this.items.map {
        HeldOrderItemResponse(
            productId = it.product.id,
            productName = it.product.name,
            quantity = it.quantity,
            price = it.price
        )
    },
    createdAt = DateUtils.convertToStandard(this.createdAt)
)