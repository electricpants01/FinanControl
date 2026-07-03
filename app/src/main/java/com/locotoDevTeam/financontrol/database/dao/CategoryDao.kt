package com.locotoDevTeam.financontrol.database.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.locotoDevTeam.financontrol.database.entity.Category

@Dao
interface CategoryDao{

    @Query("select * from category")
    fun getAll(): Flow<List<Category>>

    @Insert
    fun insert(category: Category)

    @Delete
    fun delete(category: Category)

    @Query("delete from income where categoryId = :categoryId")
    fun deleteIncomesFromACategory(categoryId: Long)

    @Query("delete from category where uid = :categoryId")
    fun deleteCategoryById(categoryId: Long)
}