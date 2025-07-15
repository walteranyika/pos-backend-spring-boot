package com.walter.pos.entities

import com.walter.pos.dtos.TaxType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(unique = true)
    var code: String,

    var name: String,
    var barcode: String?=null,
    var cost: BigDecimal,
    var price: BigDecimal,
    var isVariablePriced: Boolean,

    @ManyToOne
    @JoinColumn(name = "sale_unit_id", nullable = false)
    var saleUnit: ProductUnit,

    @ManyToOne
    @JoinColumn(name = "purchase_unit_id", nullable = false)
    var purchaseUnit: ProductUnit,


    var stockAlert: BigDecimal= BigDecimal.valueOf(0.0),

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @Enumerated(EnumType.STRING)
    var taxMethod: TaxType= TaxType.INCLUSIVE,

    var image: String?=null,
    var isActive: Boolean,
    var note: String?=null,
    val popularity: Int=0,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,


    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
)
