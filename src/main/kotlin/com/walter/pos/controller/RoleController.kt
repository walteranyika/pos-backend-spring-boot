package com.walter.pos.controller

import com.walter.pos.dtos.AssignPermissionsToRoleRequest
import com.walter.pos.dtos.RoleRequest
import com.walter.pos.service.RoleService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/roles")
class RoleController(private val roleService: RoleService) {

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    fun createRole(@Valid @RequestBody request: RoleRequest) =
        ResponseEntity.ok(roleService.createRole(request))

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    fun getAllRoles() = ResponseEntity.ok(roleService.getAllRoles())

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    fun assignPermissions(
        @PathVariable roleId: Long,
        @Valid @RequestBody request: AssignPermissionsToRoleRequest
    ) = ResponseEntity.ok(roleService.assignPermissionsToRole(roleId, request))
}