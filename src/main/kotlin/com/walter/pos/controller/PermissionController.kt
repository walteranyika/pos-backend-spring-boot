package com.walter.pos.controller

import com.walter.pos.dtos.PermissionRequest
import com.walter.pos.service.PermissionService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/permissions")
class PermissionController(private val permissionService: PermissionService) {

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    fun createPermission(@Valid @RequestBody request: PermissionRequest) =
        ResponseEntity.ok(permissionService.createPermission(request))

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    fun getAllPermissions() = ResponseEntity.ok(permissionService.getAllPermissions())
}