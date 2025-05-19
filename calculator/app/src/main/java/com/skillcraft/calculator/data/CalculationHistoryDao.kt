package com.skillcraft.calculator.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CalculationHistoryDao {
    
    @Insert
    suspend fun insert(calculationHistory: CalculationHistory)
    
    @Query("SELECT * FROM calculation_history ORDER BY id DESC")
    fun getAllCalculations(): LiveData<List<CalculationHistory>>
    
    @Query("DELETE FROM calculation_history")
    suspend fun deleteAll()
}
