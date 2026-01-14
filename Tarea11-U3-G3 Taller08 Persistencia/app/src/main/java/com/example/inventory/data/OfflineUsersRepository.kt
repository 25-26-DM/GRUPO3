package com.example.inventory.data

class OfflineUsersRepository(private val userDao: UserDao) : UsersRepository {
    override suspend fun getUser(username: String, password: String): User? = userDao.getUser(username, password)

    override suspend fun insertUser(user: User) = userDao.insert(user)
}
