package com.walter.pos.entities


import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Entity
@Table(name = "held_orders")
data class HeldOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var ref: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @OneToMany(mappedBy = "heldOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<HeldOrderItem> = mutableListOf(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,


    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
)