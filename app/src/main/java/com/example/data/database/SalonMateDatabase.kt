package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CustomerDao
import com.example.data.dao.SalonDao
import com.example.data.model.CustomerEntity
import com.example.data.model.OwnerNotificationEntity
import com.example.data.model.SalonBookingEntity
import com.example.data.model.SalonBusinessHoursEntity
import com.example.data.model.SalonEntity
import com.example.data.model.SalonServiceEntity
import com.example.data.model.SalonStaffEntity

@Database(
    entities = [
        CustomerEntity::class,
        SalonEntity::class,
        SalonServiceEntity::class,
        SalonStaffEntity::class,
        SalonBusinessHoursEntity::class,
        SalonBookingEntity::class,
        OwnerNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SalonMateDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun salonDao(): SalonDao

    companion object {
        @Volatile
        private var INSTANCE: SalonMateDatabase? = null

        fun getDatabase(context: Context): SalonMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalonMateDatabase::class.java,
                    "salonmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
