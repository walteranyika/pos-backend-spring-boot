package com.walter.pos.service

import com.walter.pos.dtos.AssignRolesToUserRequest
import com.walter.pos.dtos.CreateUserRequest
import com.walter.pos.dtos.ResetPinRequest
import com.walter.pos.dtos.UpdateUserRequest
import com.walter.pos.dtos.UserResponse
import com.walter.pos.entities.User
import com.walter.pos.repository.RoleRepository
import com.walter.pos.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.findByUsername(request.username).isPresent) {
            throw IllegalStateException("Username '${request.username}' is already taken.")
        }

        val roles = roleRepository.findAllById(request.roleIds).toMutableSet()
        if (roles.size != request.roleIds.size) {
            throw EntityNotFoundException("One or more roles not found.")
        }

        val user = User(
            username = request.username,
            pin = request.password,
            fullName = request.fullName,
            roles = roles
        )
        val savedUser = userRepository.save(user)
        return savedUser.toResponse()
    }

    @Transactional
    fun assignRolesToUser(userId: Long, request: AssignRolesToUserRequest): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val roles = roleRepository.findAllById(request.roleIds).toMutableSet()
        if (roles.size != request.roleIds.size) {
            throw EntityNotFoundException("One or more roles not found.")
        }

        user.roles.clear()
        user.roles.addAll(roles)
        val updatedUser = userRepository.save(user)
        return updatedUser.toResponse()
    }

    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    /**
     * Finds a user by their ID and resets their PIN.
     * The new PIN is encoded before being saved.
     */
    @Transactional
    fun resetPin(userId: Long, request: ResetPinRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }
        user.setPin(request.newPin)

        userRepository.save(user)
    }


    @Transactional
    fun updateUser(userId: Long, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        // Check for username uniqueness if it's being changed
        if (user.username != request.username && userRepository.findByUsername(request.username).isPresent) {
            throw IllegalStateException("Username '${request.username}' is already taken.")
        }

        user.updateUser(request.username, request.fullName)

        val updatedUser = userRepository.save(user)
        return updatedUser.toResponse()
    }


    private fun User.toResponse() = UserResponse(
        id = this.id,
        username = this.username,
        fullName = this.fullName,
        roles = this.roles.map { it.name }.toSet()
    )
}