package com.example.iobuild_kt.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProjectEntity::class, ClientEntity::class,
        DeviceEntity::class, CacheMetadata::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IoBuildDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun clientDao(): ClientDao
    abstract fun deviceDao(): DeviceDao
    abstract fun metadataDao(): MetadataDao

    companion object {
        @Volatile private var instance: IoBuildDatabase? = null
        fun getInstance(context: Context): IoBuildDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context, IoBuildDatabase::class.java, "iobuild_cache")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build().also { instance = it }
        }
    }
}
