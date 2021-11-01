package com.example.core_data.persistence.dao

import androidx.room.*
import com.example.core_data.persistence.entity.AuthEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class AuthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun inserts(vararg item: AuthEntity): List<Long>

    @Query("SELECT * FROM AuthEntity")
    abstract suspend fun selectAuth(): AuthEntity?

    @Query("SELECT * FROM AuthEntity")
    abstract fun selectAuthAsFlow(): Flow<AuthEntity?>

    @Query("DELETE FROM AuthEntity")
    abstract suspend fun deleteAll(): Int

    @Transaction
    open suspend fun replace(entity: AuthEntity) {
        deleteAll()
        inserts(entity)
    }
}