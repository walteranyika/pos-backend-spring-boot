package com.walter.pos.config

import com.walter.pos.dtos.Role
import com.walter.pos.entities.User
import com.walter.pos.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataSeeder(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (userRepository.findByUsername("cashier01").isEmpty) {
            val testUser = User(
                firstName = "Walter",
                lastName = "Nokia",
                username = "cashier01",
                pin = passwordEncoder.encode("4321"),
                role = Role.ADMIN)
            this.userRepository.save(testUser)
        }
    }
}