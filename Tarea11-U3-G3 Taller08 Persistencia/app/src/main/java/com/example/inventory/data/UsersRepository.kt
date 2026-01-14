package com.example.inventory.data

interface UsersRepository {
    suspend fun getUser(username: String, password: String): User?
    suspend fun insertUser(user: User)
}
