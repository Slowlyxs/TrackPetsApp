package com.trackpets.app.domain.usecases.user

import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.User
import com.trackpets.app.domain.repository.UserRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(page: Int = 1, search: String? = null): Result<PaginatedResponse<User>> = repository.getAll(page, search)
}

class GetUserByIdUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int): Result<User> = repository.getById(id)
}

class CreateUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(user: User, password: String = "admin123"): Result<User> = repository.create(user, password)
}

class UpdateUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int, user: User): Result<User> = repository.update(id, user)
}

class DeleteUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}

class ToggleUserActiveUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int): Result<Map<String, Any>> = repository.toggleActive(id)
}

class GetUserStatsUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<Map<String, Int>> = repository.getStats()
}

class UpdateProfileUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(data: Map<String, String>): Result<User> = repository.updateProfile(data)
}

class ChangePasswordUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(data: Map<String, String>): Result<Map<String, String>> = repository.changePassword(data)
}
