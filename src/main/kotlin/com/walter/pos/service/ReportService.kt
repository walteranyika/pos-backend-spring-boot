package com.walter.pos.service

import com.walter.pos.dtos.SaleResponse
import com.walter.pos.mappers.toResponse
import com.walter.pos.repository.SaleRepository
import com.walter.pos.specifications.SaleSpecifications
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReportService(private val saleRepository: SaleRepository) {

    fun getSalesSummary(
        startDate: LocalDate?,
        endDate: LocalDate?,
        query: String?,
        pageable: Pageable
    ): Page<SaleResponse> {
        val spec = SaleSpecifications.withDynamicQuery(startDate, endDate, query)
        return saleRepository.findAll(spec, pageable).map { it.toResponse() }
    }
}