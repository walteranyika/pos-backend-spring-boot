package com.walter.pos.entities


import jakarta.persistence.*

@Entity
@Table(name = "permissions")
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String // e.g., "CREATE_USER", "VIEW_REPORTS"
)