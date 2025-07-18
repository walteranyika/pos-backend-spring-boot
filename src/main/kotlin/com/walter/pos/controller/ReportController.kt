package com.walter.pos.controller

import com.walter.pos.dtos.SaleResponse
import com.walter.pos.service.ReportService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/services")
class ReportController(private val reportService: ReportService) {

    @GetMapping("/sales/recent")
    fun getRecentSales(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?,
        @RequestParam(name = "q", required = false) query: String?,
        @PageableDefault(size = 15, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<SaleResponse>> {
        val salesPage = reportService.getSalesSummary(startDate, endDate, query, pageable)
        return ResponseEntity.ok(salesPage)
    }
}