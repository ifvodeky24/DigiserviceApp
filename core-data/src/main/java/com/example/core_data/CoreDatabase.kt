package com.example.core_data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core_data.persistence.dao.AuthDao
import com.example.core_data.persistence.entity.auth.AuthEntity

@Database(
    entities = [
        AuthEntity::class,
//        JenisHpEntity::class
    ],
    version = BuildConfig.schemaDatabaseVersion,
)

internal abstract class CoreDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
//    abstract fun jenisHpDao(): JenisHpDao
}