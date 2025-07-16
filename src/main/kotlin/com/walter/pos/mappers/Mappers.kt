package com.walter.pos.mappers

import com.walter.pos.dtos.CategoryResponse
import com.walter.pos.dtos.ProductUnitResponse
import com.walter.pos.entities.Category
import com.walter.pos.entities.ProductUnit

fun Category.toResponse() = CategoryResponse(
    id = this.id,
    name = this.name,
    code = this.code,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun ProductUnit.toResponse() = ProductUnitResponse(
    id = this.id,
    name = this.name,
    shortName = this.shortName
)