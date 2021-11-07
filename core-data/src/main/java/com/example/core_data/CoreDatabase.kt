package com.example.core_data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core_data.persistence.dao.AuthDao
import com.example.core_data.persistence.dao.TechnicianDao
import com.example.core_data.persistence.entity.auth.AuthEntity
import com.example.core_data.persistence.entity.technician.NearbyTechnicianEntity
import com.example.core_data.persistence.entity.technician.TechnicianGetAllEntity

@Database(
    entities = [
        AuthEntity::class,
        TechnicianGetAllEntity::class,
        NearbyTechnicianEntity::class
    ],
    version = BuildConfig.schemaDatabaseVersion,
)

internal abstract class CoreDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun technicianDao(): TechnicianDao
}