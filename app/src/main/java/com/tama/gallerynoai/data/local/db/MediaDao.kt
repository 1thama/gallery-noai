package com.tama.gallerynoai.data.local.db

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY dateModified DESC")
    fun getAllPagedDateNewest(): PagingSource<Int, MediaEntity>

    @Query("SELECT * FROM media_items ORDER BY dateModified ASC")
    fun getAllPagedDateOldest(): PagingSource<Int, MediaEntity>

    @Query("SELECT * FROM media_items ORDER BY size DESC")
    fun getAllPagedSizeLargest(): PagingSource<Int, MediaEntity>

    @Query("SELECT * FROM media_items ORDER BY size ASC")
    fun getAllPagedSizeSmallest(): PagingSource<Int, MediaEntity>

    @Query("SELECT * FROM media_items WHERE bucketId = :bucketId ORDER BY dateModified DESC")
    fun getByAlbumPaged(bucketId: String): PagingSource<Int, MediaEntity>

    @Query("SELECT * FROM media_items WHERE isFavorite = 1 ORDER BY dateModified DESC")
    fun getFavoritesPaged(): PagingSource<Int, MediaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaEntity>)

    @Update
    suspend fun updateAll(items: List<MediaEntity>)

    @Query("DELETE FROM media_items")
    suspend fun deleteAll()

    @Query("DELETE FROM media_items WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Update
    suspend fun update(item: MediaEntity)

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaEntity?

    @Query("SELECT COUNT(*) FROM media_items WHERE isVideo = 0")
    fun getTotalImageCount(): Flow<Int>

    @Query("SELECT * FROM media_items")
    suspend fun getAll(): List<MediaEntity>

    @Query("SELECT * FROM media_items ORDER BY dateModified DESC")
    fun getAllFlow(): Flow<List<MediaEntity>>

    @Query("SELECT id, dateModified FROM media_items")
    suspend fun getAllIdsAndDates(): List<MediaIdAndDate>

    @Query("SELECT customTags FROM media_items")
    suspend fun getAllCustomTags(): List<String>

    @Query("""
        SELECT media_items.* FROM media_items 
        JOIN media_items_fts ON media_items.id = media_items_fts.rowid 
        WHERE media_items_fts MATCH :query
    """)
    fun search(query: String): Flow<List<MediaEntity>>
}
