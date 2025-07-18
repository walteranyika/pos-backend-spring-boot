package com.walter.pos.specifications

import com.walter.pos.entities.Product
import com.walter.pos.entities.Sale
import com.walter.pos.entities.SaleItem
import com.walter.pos.entities.User
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.time.LocalTime

object SaleSpecifications {

    fun withDynamicQuery(
        startDate: LocalDate?,
        endDate: LocalDate?,
        query: String?
    ): Specification<Sale> {
        return Specification { root, criteriaQuery, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            startDate?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), it.atStartOfDay()))
            }
            endDate?.let {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), it.atTime(LocalTime.MAX)))
            }

            query?.takeIf { it.isNotBlank() }?.let {
                val searchTerm = "%${it.lowercase()}%"
                val userJoin = root.join<Sale, User>("user", JoinType.LEFT)
                val itemJoin = root.join<Sale, SaleItem>("items", JoinType.LEFT)
                val productJoin = itemJoin.join<SaleItem, Product>("product", JoinType.LEFT)

                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("username")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("name")), searchTerm)
                ))
            }

            criteriaQuery?.distinct(true)
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}