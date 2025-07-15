package com.walter.pos.dtos

enum class Role {
    USER,
    ADMIN
}

enum class TaxType {
    INCLUSIVE,
    EXCLUSIVE
}


enum class PaymentStatus{
    PAID,
    PENDING,
    PARTIAL,
    UNPAID,
}


enum class PaymentMethod{
    MPESA,
    CASH,
    CREDIT,
    CARD,
    COMPLIMENTARY,
}