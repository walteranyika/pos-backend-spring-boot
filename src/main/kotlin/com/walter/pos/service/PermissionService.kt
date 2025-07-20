package com.walter.pos.service


import com.walter.pos.dtos.PermissionRequest
import com.walter.pos.dtos.PermissionResponse
import com.walter.pos.entities.Permission
import com.walter.pos.repository.PermissionRepository
import org.springframework.stereotype.Service

@Service
class PermissionService(private val permissionRepository: PermissionRepository) {

    fun createPermission(request: PermissionRequest): PermissionResponse {
        val permission = Permission(name = request.name.uppercase())
        val savedPermission = permissionRepository.save(permission)
        return savedPermission.toResponse()
    }

    fun getAllPermissions(): List<PermissionResponse> {
        return permissionRepository.findAll().map { it.toResponse() }
    }

    private fun Permission.toResponse() = PermissionResponse(id = this.id, name = this.name)
}
