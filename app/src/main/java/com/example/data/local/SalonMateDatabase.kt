package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CustomerEntity

@Database(entities = [CustomerEntity::class], version = 2, exportSchema = false)
abstract class SalonMateDatabase : RoomDatabase() {

  abstract fun customerDao(): CustomerDao

  companion object {
    @Volatile
    private var INSTANCE: SalonMateDatabase? = null

    fun getDatabase(context: Context): SalonMateDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          SalonMateDatabase::class.java,
          "salonmate_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
