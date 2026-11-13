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

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
    version = 2,
    exportSchema = false
)
abstract class SalonMateDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun salonDao(): SalonDao

    companion object {
        @Volatile
        private var INSTANCE: SalonMateDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("PRAGMA table_info(customers)")
                var hasNormalizedPhone = false
                try {
                    val nameIndex = cursor.getColumnIndex("name")
                    while (cursor.moveToNext()) {
                        if (nameIndex >= 0 && cursor.getString(nameIndex) == "normalizedPhone") {
                            hasNormalizedPhone = true
                            break
                        }
                    }
                } finally {
                    cursor.close()
                }
                if (!hasNormalizedPhone) {
                    db.execSQL("ALTER TABLE customers ADD COLUMN normalizedPhone TEXT NOT NULL DEFAULT ''")
                    db.execSQL("UPDATE customers SET normalizedPhone = mobileNumber WHERE normalizedPhone = ''")
                }
            }
        }

        fun getDatabase(context: Context): SalonMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalonMateDatabase::class.java,
                    "salonmate_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
