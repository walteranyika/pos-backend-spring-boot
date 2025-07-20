package com.walter.pos.controller

import com.walter.pos.dtos.AssignRolesToUserRequest
import com.walter.pos.dtos.CreateUserRequest
import com.walter.pos.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    fun createUser(@Valid @RequestBody request: CreateUserRequest) =
        ResponseEntity.ok(userService.createUser(request))

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    fun getAllUsers() = ResponseEntity.ok(userService.getAllUsers())

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    fun assignRoles(
        @PathVariable userId: Long,
        @Valid @RequestBody request: AssignRolesToUserRequest
    ) = ResponseEntity.ok(userService.assignRolesToUser(userId, request))
}