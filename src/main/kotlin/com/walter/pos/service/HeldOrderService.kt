package com.walter.pos.service

import com.walter.pos.dtos.CreateSaleRequest
import com.walter.pos.dtos.HeldOrderItemResponse
import com.walter.pos.dtos.HeldOrderResponse
import com.walter.pos.dtos.HoldOrderRequest
import com.walter.pos.dtos.SaleResponse
import com.walter.pos.entities.HeldOrder
import com.walter.pos.entities.HeldOrderItem
import com.walter.pos.entities.User
import com.walter.pos.exceptions.ResourceNotFoundException
import com.walter.pos.mappers.toResponse
import com.walter.pos.repository.CustomerRepository
import com.walter.pos.repository.HeldOrderRepository
import com.walter.pos.repository.ProductRepository
import com.walter.pos.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class HeldOrderService(
    private val heldOrderRepository: HeldOrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val customerRepository: CustomerRepository,
    private val saleService: PosService // To convert held order to a sale
) {

    @Transactional
    fun createHeldOrder(request: HoldOrderRequest): HeldOrderResponse {
        val currentUser = getCurrentUser()

        val customer= customerRepository.findById(request.customerId).orElseThrow{
            ResourceNotFoundException("Customer not found with ID: ${request.customerId}")
        }

        val heldOrder = HeldOrder(
            ref = "HELD-${System.currentTimeMillis()}",
            user = currentUser,
            customer = customer
        )

        val items = request.items.map { itemRequest ->
            val product = productRepository.findById(itemRequest.productId)
                .orElseThrow { EntityNotFoundException("Product not found with ID: ${itemRequest.productId}") }

            HeldOrderItem(
                heldOrder = heldOrder,
                product = product,
                quantity = itemRequest.quantity,
                price = product.price // Capture current price
            )
        }

        heldOrder.items.addAll(items)
        val savedOrder = heldOrderRepository.save(heldOrder)
        return savedOrder.toResponse()
    }

    @Transactional
    fun updateHeldOrder(id: Long, request: HoldOrderRequest): HeldOrderResponse {
        val heldOrder = findOrderByIdAndCurrentUser(id)

        // Clear existing items and add the new ones
        heldOrder.items.clear()
        val newItems = request.items.map { itemRequest ->
            val product = productRepository.findById(itemRequest.productId)
                .orElseThrow { EntityNotFoundException("Product not found with ID: ${itemRequest.productId}") }
            HeldOrderItem(
                heldOrder = heldOrder,
                product = product,
                quantity = itemRequest.quantity,
                price = product.price
            )
        }
        heldOrder.items.addAll(newItems)

        val updatedOrder = heldOrderRepository.save(heldOrder)
        return updatedOrder.toResponse()
    }

    @Transactional(readOnly = true)
    fun getHeldOrdersForCurrentUser(): List<HeldOrderResponse> {
        val currentUser = getCurrentUser()
        return heldOrderRepository.findByUserIdOrderByIdDesc(currentUser.id).map { it.toResponse() }
    }

    @Transactional
    fun deleteHeldOrder(id: Long) {
        val heldOrder = findOrderByIdAndCurrentUser(id)
        heldOrderRepository.delete(heldOrder)
    }

    @Transactional
    fun resumeAndCompleteSale(id: Long, request: CreateSaleRequest): SaleResponse {
        val heldOrder = findOrderByIdAndCurrentUser(id)

        // Create a sale request from the held order
//        val createSaleRequest = CreateSaleRequest(
//            items = heldOrder.items.map {
//                CreateSaleItemRequest(
//                    productId = it.product.id,
//                    quantity = it.quantity
//                )
//            },
//            payments = listOf(paymentRequest)
//        )

        // Use the existing SaleService to process the sale
        val saleResponse = saleService.createSale(request, getCurrentUser())

        // The sale is complete, so delete the held order
        heldOrderRepository.delete(heldOrder)

        return saleResponse
    }

    private fun getCurrentUser(): User {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }
    }

    private fun findOrderByIdAndCurrentUser(orderId: Long): HeldOrder {
        val currentUser = getCurrentUser()
        val heldOrder = heldOrderRepository.findById(orderId)
            .orElseThrow { EntityNotFoundException("Held order not found with ID: $orderId") }

        if (heldOrder.user.id != currentUser.id) {
            throw IllegalAccessException("You are not authorized to access this order.")
        }
        return heldOrder
    }
}