package com.walter.pos.entities


import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "purchase_items")
data class PurchaseItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    var purchase: Purchase,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(nullable = false, precision = 10, scale = 2)
    val quantity: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val costPrice: BigDecimal, // Cost per unit

    @Column(nullable = false, precision = 10, scale = 2)
    val totalCost: BigDecimal
)