package com.exponential_groth.calculator.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VariablesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVariables(variables: List<Variable>)

    @Query("SELECT * FROM variable")
    suspend fun getVariables(): List<Variable>
}