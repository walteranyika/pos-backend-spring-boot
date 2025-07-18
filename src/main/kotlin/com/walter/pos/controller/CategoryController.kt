package com.walter.pos.controller

import com.walter.pos.dtos.CategoryRequest
import com.walter.pos.dtos.CategoryResponse
import com.walter.pos.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getCategories(@RequestParam(name = "q", required = false) query: String?): List<CategoryResponse> {
        return if (query.isNullOrBlank()) {
            categoryService.getAllCategories()
        } else {
            categoryService.searchCategories(query)
        }
    }
    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): CategoryResponse = categoryService.getCategoryById(id)

    @PostMapping
    fun createCategory(@RequestBody request: CategoryRequest): ResponseEntity<CategoryResponse> {
        val category = categoryService.createCategory(request)
        return ResponseEntity(category, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: Long, @RequestBody request: CategoryRequest): CategoryResponse =
        categoryService.updateCategory(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCategory(@PathVariable id: Long) = categoryService.deleteCategory(id)
}