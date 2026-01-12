package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getAllUsersStream(): Flow<List<User>>
    fun getUserStream(name: String, password: String): Flow<User?>
    suspend fun insertUser(user: User)
}
