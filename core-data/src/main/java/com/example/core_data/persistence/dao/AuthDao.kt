package com.example.core_data.persistence.dao

import androidx.room.*
import com.example.core_data.persistence.entity.auth.AuthEntity
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

    @Query("UPDATE AuthEntity set foto=:foto WHERE id=:id")
    abstract suspend fun updateFoto(id: Int, foto: String)

    @Query("UPDATE AuthEntity set teknisiIdentias=:photoIdentitas WHERE id=:id")
    abstract suspend fun updatePhotoIdentitas(id: Int, photoIdentitas: String)

    @Query("UPDATE AuthEntity set teknisiTempatUsaha=:photoTempatUsaha WHERE id=:id")
    abstract suspend fun updatePhotoTempatUsaha(id: Int, photoTempatUsaha: String)

    @Query("UPDATE AuthEntity set teknisiSertifikat=:sertifikat WHERE id=:id")
    abstract suspend fun updateSertifikat(id: Int, sertifikat: String)

    @Transaction
    open suspend fun replace(entity: AuthEntity) {
        deleteAll()
        inserts(entity)
    }
}