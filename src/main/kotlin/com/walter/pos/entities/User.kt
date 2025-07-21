package com.walter.pos.entities

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var fullName: String,

    @Column(unique = true)
    private var username: String,

    private var pin: String,

    @ManyToMany(fetch = FetchType.EAGER) // Eager fetch is crucial for security
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf()

) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        // Authorities are the permissions granted to the user through their roles.
        return roles.flatMap { it.permissions }
            .map { SimpleGrantedAuthority(it.name) }
            .toSet()
    }

    override fun getPassword(): String {
        return pin
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    fun setPin(newPin: String) {
        this.pin = newPin
    }

    fun updateUser(username: String, fullName: String) {
        this.username = username
        this.fullName = fullName
    }


}