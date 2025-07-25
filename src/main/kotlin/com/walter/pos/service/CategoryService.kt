package com.walter.pos.service

import com.walter.pos.dtos.CategoryRequest
import com.walter.pos.dtos.CategoryResponse
import com.walter.pos.entities.Category
import com.walter.pos.exceptions.ResourceNotFoundException
import com.walter.pos.mappers.toResponse
import com.walter.pos.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getAllCategories(): List<CategoryResponse> =
        categoryRepository.findAll().map { it.toResponse() }

    fun searchCategories(query: String): List<CategoryResponse> =
        categoryRepository.search(query).map { it.toResponse() }

    fun getCategoryById(id: Long): CategoryResponse =
        findCategoryById(id).toResponse()


    fun createCategory(request: CategoryRequest): CategoryResponse {
        val category = Category(name = request.name, code = request.code)
        return categoryRepository.save(category).toResponse()
    }

    fun updateCategory(id: Long, request: CategoryRequest): CategoryResponse {
        val category = findCategoryById(id)
        category.name = request.name
        category.code = request.code
        return categoryRepository.save(category).toResponse()
    }

    fun deleteCategory(id: Long) {
        if (!categoryRepository.existsById(id)) {
            throw ResourceNotFoundException("Category with ID $id not found.")
        }
        categoryRepository.deleteById(id)
    }

     fun findCategoryById(id: Long): Category =
        categoryRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Category with ID $id not found.") }

}