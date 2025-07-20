package com.walter.pos.dtos

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

// --- Permission DTOs ---
data class PermissionRequest(
    @field:NotEmpty
    val name: String
)

data class PermissionResponse(
    val id: Long,
    val name: String
)

// --- Role DTOs ---
data class RoleRequest(
    @field:NotEmpty
    val name: String
)

data class RoleResponse(
    val id: Long,
    val name: String,
    val permissions: Set<String>
)

// --- User DTOs ---
data class CreateUserRequest(
    @field:NotEmpty
    val username: String,
    @field:NotEmpty
    @field:Size(min = 4, message = "Password must be 4 digit characters")
    val password: String,
    val fullName: String,
    val roleIds: Set<Long> = emptySet()
)

data class UserResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val roles: Set<String>
)

// --- Assignment DTOs ---
data class AssignPermissionsToRoleRequest(
    @field:NotEmpty
    val permissionIds: Set<Long>
)

data class AssignRolesToUserRequest(
    @field:NotEmpty
    val roleIds: Set<Long>
)