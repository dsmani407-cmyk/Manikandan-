package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CachedLeadEntity::class,
        CachedSalesTransactionEntity::class,
        CachedConfirmedGuestEntity::class,
        CachedDailyTaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CrmDatabase : RoomDatabase() {

    abstract fun crmDao(): CrmDao

    companion object {
        @Volatile
        private var INSTANCE: CrmDatabase? = null

        fun getDatabase(context: Context): CrmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrmDatabase::class.java,
                    "field_sales_crm.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
