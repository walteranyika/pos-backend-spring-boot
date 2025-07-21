package com.walter.pos.config

import com.walter.pos.entities.Permission
import com.walter.pos.entities.Role
import com.walter.pos.entities.User
import com.walter.pos.repository.PermissionRepository
import com.walter.pos.repository.RoleRepository
import com.walter.pos.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataSeeder(
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        logger.info("Starting data seeding process...")

        val allPermissions = seedPermissions()
        val adminRole = seedRoles(allPermissions)
        seedAdminUser(adminRole)

        logger.info("Data seeding process finished successfully.")
    }


    private fun seedPermissions(): Set<Permission> {
        val permissionsToSeed = setOf(
            // User Management
            "MANAGE_USERS", "CREATE_USERS", "VIEW_USERS",
            // Role & Permission Management
            "MANAGE_ROLES", "MANAGE_PERMISSIONS",
            // Product Management
            "MANAGE_PRODUCTS", "CREATE_PRODUCTS", "UPDATE_PRODUCTS", "DELETE_PRODUCTS", "VIEW_PRODUCTS",
            // Stock Management
            "MANAGE_STOCK", "ADJUST_STOCK",
            // Sales & Orders
            "CREATE_SALES", "VIEW_SALES_REPORTS", "HOLD_ORDERS", "RESUME_ORDERS",
            // Other Entities
            "MANAGE_CATEGORIES", "MANAGE_UNITS"
        )

        val existingPermissions = permissionRepository.findAll().map { it.name }.toSet()
        val newPermissions = permissionsToSeed
            .filterNot { existingPermissions.contains(it) }
            .map { Permission(name = it) }

        if (newPermissions.isNotEmpty()) {
            permissionRepository.saveAll(newPermissions)
            logger.info("Seeded ${newPermissions.size} new permissions.")
        }

        return permissionRepository.findAll().toSet()
    }

    private fun seedRoles(allPermissions: Set<Permission>): Role {
        // --- ADMIN Role ---
        // The ADMIN role gets all permissions that exist in the system.
        val adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet {
            logger.info("Creating ROLE_ADMIN...")
            Role(name = "ROLE_ADMIN")
        }
        // This ensures the admin role always has every permission, even new ones.
        adminRole.permissions.clear()
        adminRole.permissions.addAll(allPermissions)
        roleRepository.save(adminRole)

        // --- CASHIER Role ---
        // The CASHIER role gets a specific, limited set of permissions.
        val cashierPermissions = setOf(
            "CREATE_SALES",
            "VIEW_PRODUCTS",
            "HOLD_ORDERS",
            "RESUME_ORDERS"
        )
        val cashierRole = roleRepository.findByName("ROLE_CASHIER").orElseGet {
            logger.info("Creating ROLE_CASHIER...")
            Role(name = "ROLE_CASHIER")
        }
        val permissionsForCashier = allPermissions.filter { cashierPermissions.contains(it.name) }.toSet()
        cashierRole.permissions.clear()
        cashierRole.permissions.addAll(permissionsForCashier)
        roleRepository.save(cashierRole)

        return adminRole
    }

    private fun seedAdminUser(adminRole: Role) {
        val adminUsername = "admin"
        if (userRepository.findByUsername(adminUsername).isEmpty) {
            logger.info("Creating default admin user...")
            val adminUser = User(
                fullName = "Tom Heaton",
                username = adminUsername,
                pin = "1234", // Default PIN/Password
                roles = mutableSetOf(adminRole)
            )
            userRepository.save(adminUser)
            logger.info("Default admin user created with username '{}' and default PIN.", adminUsername)
        }
    }
}