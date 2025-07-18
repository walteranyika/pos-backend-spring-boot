package com.walter.pos.entities

import com.walter.pos.dtos.PaymentStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "sales")
data class Sale(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val ref: String,

    val grandTotal: BigDecimal,
    val discount: BigDecimal=0.toBigDecimal(),
    val paidAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    val paymentStatus: PaymentStatus,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @OneToMany(mappedBy = "sale", cascade = [CascadeType.ALL], orphanRemoval = true)
    val details: List<SaleItem> = listOf(),

    @OneToMany(mappedBy = "sale", cascade = [CascadeType.ALL], orphanRemoval = true)
    val payments: List<PaymentSale> = listOf(),

    val isCreditSale: Boolean=false,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,


    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
)
