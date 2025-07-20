package com.walter.pos.repository

import com.walter.pos.entities.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<Role, Long>{
    fun findByName(name: String): Optional<Role>
}