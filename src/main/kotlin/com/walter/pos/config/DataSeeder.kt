package com.walter.pos.config

import com.walter.pos.entities.*
import com.walter.pos.repository.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.lang.Math.random
import java.math.BigDecimal

@Component
class DataSeeder(
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository,

    // --- Add these new dependencies ---
    private val categoryRepository: CategoryRepository,
    private val unitRepository: ProductUnitRepository,
    private val productRepository: ProductRepository,

    private val stockRepository: StockRepository
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        logger.info("Starting data seeding process...")

        val allPermissions = seedPermissions()
        val adminRole = seedRoles(allPermissions)
        seedAdminUser(adminRole)
        seedCategoriesAndUnits()
        seedProducts()

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

    private fun seedCategoriesAndUnits() {
        logger.info("Seeding Categories and Units...")

        // Seed Units
        val unitsToSeed = mapOf(
            "Kgs" to "Kilograms",
            "Btl" to "Bottles",
            "Pts" to "Packets",
            "Pcs" to "Pieces"
        )
        unitsToSeed.mapValues { (abbr, name) ->
            unitRepository.findByName(name).orElseGet {
                unitRepository.save(ProductUnit(name = name, shortName = abbr))
            }
        }

        // Seed Categories
        val categoriesToSeed = listOf("Cereals", "Drinks", "Foods")
        categoriesToSeed.associateWith { catName ->
            categoryRepository.findByName(catName).orElseGet {
                categoryRepository.save(Category(name = catName, code = (1000..1100).random().toString()))
            }
        }
        logger.info("Categories and Units seeding complete.")
    }

    private fun seedProducts() {
        if (productRepository.count() > 0) {
            logger.info("Products already exist. Skipping product seeding.")
            return
        }

        logger.info("Seeding 20 dummy products...")

        val productsToSeed = listOf(
            // Cereals (5)
            Product(
                name = "Corn Flakes 500g",
                price = 250.0.toBigDecimal(),
                code = "001",
                barcode = "1234567890",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(3.toLong()).get(),
                category = categoryRepository.findById(1.toLong()).get(),
                purchaseUnit = unitRepository.findById(3.toLong()).get(),
                isActive = true
            ),

            Product(
                name = "Weetabix Cereal",
                price = 320.0.toBigDecimal(),
                code = "002",
                barcode = "1234567891",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(3.toLong()).get(),
                category = categoryRepository.findById(1.toLong()).get(),
                purchaseUnit = unitRepository.findById(3.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Oatmeal Instant Pack",
                price = 180.0.toBigDecimal(),
                code = "003",
                barcode = "1234567892",
                cost = 79.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(3.toLong()).get(),
                category = categoryRepository.findById(1.toLong()).get(),
                purchaseUnit = unitRepository.findById(3.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Rice Krispies",
                price = 280.0.toBigDecimal(),
                code = "004",
                barcode = "1234567893",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(3.toLong()).get(),
                category = categoryRepository.findById(1.toLong()).get(),
                purchaseUnit = unitRepository.findById(3.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Granola with Nuts",
                price = 450.0.toBigDecimal(),
                code = "005",
                barcode = "1234567894",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(3.toLong()).get(),
                category = categoryRepository.findById(1.toLong()).get(),
                purchaseUnit = unitRepository.findById(3.toLong()).get(),
                isActive = true
            ),

            // Drinks (5)
            Product(
                name = "Mineral Water 1L",
                price = 100.0.toBigDecimal(),
                code = "006",
                barcode = "1234567895",
                cost = 66.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(1.toLong()).get(),
                category = categoryRepository.findById(1.toLong()).get(),
                purchaseUnit = unitRepository.findById(1.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Orange Juice 1L",
                price = 220.0.toBigDecimal(),
                code = "007",
                barcode = "1234567896",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(2.toLong()).get(),
                category = categoryRepository.findById(2.toLong()).get(),
                purchaseUnit = unitRepository.findById(2.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Apple Juice 1L",
                price = 230.0.toBigDecimal(),
                code = "008",
                barcode = "1234567897",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(3.toLong()).get(),
                category = categoryRepository.findById(2.toLong()).get(),
                purchaseUnit = unitRepository.findById(3.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Fresh Milk 1L",
                price = 150.0.toBigDecimal(),
                code = "009",
                barcode = "1234567898",
                cost = 90.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(3.toLong()).get(),
                category = categoryRepository.findById(2.toLong()).get(),
                purchaseUnit = unitRepository.findById(3.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Soda 2L",
                price = 180.0.toBigDecimal(),
                code = "010",
                barcode = "1234567899",
                cost = 43.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(2.toLong()).get(),
                category = categoryRepository.findById(2.toLong()).get(),
                purchaseUnit = unitRepository.findById(2.toLong()).get(),
                isActive = true
            ),

            // Foods (10)
            Product(
                name = "Sugar 1Kg",
                price = 160.0.toBigDecimal(),
                code = "011",
                barcode = "12345678100",
                cost = 100.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(1.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(1.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Basmati Rice 1Kg",
                price = 240.0.toBigDecimal(),
                code = "012",
                barcode = "12345678101",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(1.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(1.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "All-Purpose Flour 1Kg",
                price = 140.0.toBigDecimal(),
                code = "013",
                barcode = "12345678102",
                cost = 76.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(1.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(1.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Cooking Oil 1L",
                price = 350.0.toBigDecimal(),
                code = "014",
                barcode = "12345678103",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(4.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(4.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Table Salt 500g",
                price = 110.0.toBigDecimal(),
                code = "015",
                barcode = "12345678104",
                cost = 75.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(3.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(3.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Loaf of Bread",
                price = 120.0.toBigDecimal(),
                code = "016",
                barcode = "12345678105",
                cost = 90.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(4.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(4.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Dozen Eggs",
                price = 320.0.toBigDecimal(),
                code = "017",
                barcode = "12345678106",
                cost = 200.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(4.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(4.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Spaghetti Pasta 500g",
                price = 190.0.toBigDecimal(),
                code = "020",
                barcode = "12345678107",
                cost = 121.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(4.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(4.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Canned Tuna in Oil",
                price = 210.0.toBigDecimal(),
                code = "018",
                barcode = "12345678108",
                cost = 142.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(4.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(4.toLong()).get(),
                isActive = true
            ),
            Product(
                name = "Instant Noodles 5-Pack",
                price = 400.0.toBigDecimal(),
                code = "019",
                barcode = "12345678109",
                cost = 310.toBigDecimal(),
                isVariablePriced = false,
                saleUnit = unitRepository.findById(4.toLong()).get(),
                category = categoryRepository.findById(3.toLong()).get(),
                purchaseUnit = unitRepository.findById(4.toLong()).get(),
                isActive = true
            )
        )

        val results = productRepository.saveAll(productsToSeed)
        results.forEach { it ->
            val stock = Stock(product = it, quantity = BigDecimal.ZERO)
            stockRepository.save(stock)
        }
        logger.info("Successfully seeded ${productsToSeed.size} products.")
    }
}