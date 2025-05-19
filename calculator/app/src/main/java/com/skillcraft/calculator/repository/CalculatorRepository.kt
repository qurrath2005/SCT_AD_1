package com.skillcraft.calculator.repository

import androidx.lifecycle.LiveData
import com.skillcraft.calculator.data.CalculationHistory
import com.skillcraft.calculator.data.CalculationHistoryDao

class CalculatorRepository(private val calculationHistoryDao: CalculationHistoryDao) {
    
    val allCalculations: LiveData<List<CalculationHistory>> = calculationHistoryDao.getAllCalculations()
    
    suspend fun insert(calculationHistory: CalculationHistory) {
        calculationHistoryDao.insert(calculationHistory)
    }
    
    suspend fun clearHistory() {
        calculationHistoryDao.deleteAll()
    }
}
