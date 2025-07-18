package com.walter.pos.service


import com.walter.pos.dtos.ProductUnitRequest
import com.walter.pos.dtos.ProductUnitResponse
import com.walter.pos.entities.ProductUnit
import com.walter.pos.exceptions.ResourceNotFoundException
import com.walter.pos.repository.ProductUnitRepository
import org.springframework.stereotype.Service

@Service
class ProductUnitService(private val unitRepository: ProductUnitRepository) {

    fun getAllUnits(): List<ProductUnitResponse> =
        unitRepository.findAll().map { it.toResponse() }

    fun getUnitById(id: Long): ProductUnitResponse =
        findUnitById(id).toResponse()

    fun createUnit(request: ProductUnitRequest): ProductUnitResponse {
        val unit = ProductUnit(name = request.name, shortName = request.shortName)
        return unitRepository.save(unit).toResponse()
    }

    fun updateUnit(id: Long, request: ProductUnitRequest): ProductUnitResponse {
        val unit = findUnitById(id)
        unit.name = request.name
        unit.shortName = request.shortName
        return unitRepository.save(unit).toResponse()
    }

    fun deleteUnit(id: Long) {
        if (!unitRepository.existsById(id)) {
            throw ResourceNotFoundException("Unit with ID $id not found.")
        }
        unitRepository.deleteById(id)
    }
    fun searchUnits(query: String): List<ProductUnitResponse> =
        unitRepository.search(query).map { it.toResponse() }

    internal fun findUnitById(id: Long): ProductUnit =
        unitRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Unit with ID $id not found.") }

    private fun ProductUnit.toResponse() = ProductUnitResponse(
        id = this.id,
        name = this.name,
        shortName = this.shortName,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}