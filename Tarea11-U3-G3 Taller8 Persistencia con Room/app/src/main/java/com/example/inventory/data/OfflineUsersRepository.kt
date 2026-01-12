package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

class OfflineUsersRepository(private val userDao: UserDao) : UsersRepository {
    override fun getAllUsersStream(): Flow<List<User>> = userDao.getAllUsers()
    override fun getUserStream(name: String, password: String): Flow<User?> = userDao.getUser(name, password)
    override suspend fun insertUser(user: User) = userDao.insert(user)
}
