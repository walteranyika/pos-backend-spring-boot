package com.walter.pos.service

import com.walter.pos.dtos.CreateCustomerRequest
import com.walter.pos.dtos.CustomerResponse
import com.walter.pos.entities.Customer
import com.walter.pos.mappers.toResponse
import com.walter.pos.repository.CustomerRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class CustomerService(private val customerRepository: CustomerRepository) {

    fun getAllCustomers(): List<CustomerResponse> {
        return customerRepository.findAll(Sort.by("name")).map { it.toResponse() }
    }

    fun createCustomer(request: CreateCustomerRequest): CustomerResponse {
        if (!request.phoneNumber.isNullOrBlank() && customerRepository.findByPhoneNumber(request.phoneNumber).isPresent) {
            throw IllegalStateException("A customer with phone number '${request.phoneNumber}' already exists.")
        }

        val customer = Customer(
            name = request.name,
            phoneNumber = request.phoneNumber?.ifBlank { null }
        )
        val savedCustomer = customerRepository.save(customer)
        return savedCustomer.toResponse()
    }
}