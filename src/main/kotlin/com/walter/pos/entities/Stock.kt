package com.walter.pos.entities


import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "stocks")
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    val product: Product,

    @Column(nullable = false, precision = 10, scale = 2)
    var quantity: BigDecimal = BigDecimal.ZERO,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,


    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
)