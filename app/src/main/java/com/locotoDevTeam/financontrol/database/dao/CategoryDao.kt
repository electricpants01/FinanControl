package com.locotoDevTeam.financontrol.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.locotoDevTeam.financontrol.database.entity.Category

@Dao
interface CategoryDao{

    @Query("select * from category")
    fun getAll(): LiveData<List<Category>>

    @Insert
    fun insert(category: Category)

    //TODO: el delelte tiene q eliminar todos los datos de las tablas de ingresos/egresos
    @Delete
    fun delete(category: Category)
}