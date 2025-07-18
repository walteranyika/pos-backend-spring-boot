package com.walter.pos.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Entity
@Table(name = "purchases")
data class Purchase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var ref: String,

    var supplier: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    var totalCost: BigDecimal,

    @CreationTimestamp
    val purchaseDate: OffsetDateTime? = null,

    @OneToMany(mappedBy = "purchase", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<PurchaseItem> = mutableListOf(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,


    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
)