package com.org.labss.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ECDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}