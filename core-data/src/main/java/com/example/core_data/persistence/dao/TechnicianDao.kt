package com.example.core_data.persistence.dao

import androidx.room.*
import com.example.core_data.persistence.entity.technician.TechnicianGetAllEntities
import com.example.core_data.persistence.entity.technician.TechnicianGetAllEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class TechnicianDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTechnicianGetAll(vararg item: TechnicianGetAllEntity): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTechnicianGetAll(items: TechnicianGetAllEntities): List<Long>

    @Query("SELECT * FROM TechnicianGetAllEntity")
    abstract suspend fun selectTechnicianGetAll(): TechnicianGetAllEntities

    @Query("SELECT * FROM TechnicianGetAllEntity")
    abstract fun selectTechnicianGetAllAsFlow(): Flow<TechnicianGetAllEntities>

    @Query("DELETE FROM TechnicianGetAllEntity")
    abstract suspend fun deleteTechnicianGetAll(): Int

    @Transaction
    open suspend fun replace(entities: TechnicianGetAllEntities) {
        deleteTechnicianGetAll()
        insertTechnicianGetAll(entities)
    }

}