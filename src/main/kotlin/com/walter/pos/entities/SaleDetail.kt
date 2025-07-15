package com.walter.pos.entities

import com.walter.pos.dtos.PaymentStatus
import com.walter.pos.dtos.TaxType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "sale_details")
data class SaleDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val ref: String,

    val quantity: BigDecimal,
    val price: BigDecimal,
    val discount: BigDecimal=0.toBigDecimal(),
    val total: BigDecimal,


    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    val sale: Sale,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,


    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
)