package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("SELECT * from users WHERE name = :name AND password = :password")
    fun getUser(name: String, password: String): Flow<User?>

    @Query("SELECT * from users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>
}
