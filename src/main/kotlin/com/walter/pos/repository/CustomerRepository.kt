package com.walter.pos.repository

import com.walter.pos.entities.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByName(name: String): Optional<Customer>
    fun findByPhoneNumber(phoneNumber: String): Optional<Customer>
}