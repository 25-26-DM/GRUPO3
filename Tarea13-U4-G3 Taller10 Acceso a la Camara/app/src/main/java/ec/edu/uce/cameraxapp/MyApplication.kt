package ec.edu.uce.cameraxapp

import android.app.Application
import ec.edu.uce.cameraxapp.data.local.AppDatabase
import ec.edu.uce.cameraxapp.repository.UserRepository

class MyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao()) }
}