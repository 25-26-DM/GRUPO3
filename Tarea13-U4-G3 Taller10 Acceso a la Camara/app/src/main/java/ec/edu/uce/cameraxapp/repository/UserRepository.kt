package ec.edu.uce.cameraxapp.repository

import ec.edu.uce.cameraxapp.data.local.User
import ec.edu.uce.cameraxapp.data.local.UserDao

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
}
