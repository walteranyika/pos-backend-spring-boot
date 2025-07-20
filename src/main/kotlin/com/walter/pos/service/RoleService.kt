package com.walter.pos.service

import com.walter.pos.dtos.AssignPermissionsToRoleRequest
import com.walter.pos.dtos.RoleRequest
import com.walter.pos.dtos.RoleResponse
import com.walter.pos.entities.Role
import com.walter.pos.repository.PermissionRepository
import com.walter.pos.repository.RoleRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) {

    @Transactional
    fun createRole(request: RoleRequest): RoleResponse {
        val role = Role(name = request.name.uppercase())
        val savedRole = roleRepository.save(role)
        return savedRole.toResponse()
    }

    @Transactional
    fun assignPermissionsToRole(roleId: Long, request: AssignPermissionsToRoleRequest): RoleResponse {
        val role = roleRepository.findById(roleId)
            .orElseThrow { EntityNotFoundException("Role not found with ID: $roleId") }

        val permissions = permissionRepository.findAllById(request.permissionIds).toMutableSet()
        if (permissions.size != request.permissionIds.size) {
            throw EntityNotFoundException("One or more permissions not found.")
        }

        role.permissions.clear()
        role.permissions.addAll(permissions)
        val updatedRole = roleRepository.save(role)
        return updatedRole.toResponse()
    }

    fun getAllRoles(): List<RoleResponse> {
        return roleRepository.findAll().map { it.toResponse() }
    }

    private fun Role.toResponse() = RoleResponse(
        id = this.id,
        name = this.name,
        permissions = this.permissions.map { it.name }.toSet()
    )
}