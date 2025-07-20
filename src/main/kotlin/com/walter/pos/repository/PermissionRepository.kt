package com.walter.pos.repository


import com.walter.pos.entities.Permission
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PermissionRepository : JpaRepository<Permission, Long>{
    fun findByName(name: String): Optional<Permission>
}
