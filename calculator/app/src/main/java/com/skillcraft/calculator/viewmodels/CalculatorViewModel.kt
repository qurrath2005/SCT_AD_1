package com.skillcraft.calculator.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.skillcraft.calculator.data.CalculationHistory
import com.skillcraft.calculator.data.CalculatorDatabase
import com.skillcraft.calculator.repository.CalculatorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: CalculatorRepository
    val allCalculations: LiveData<List<CalculationHistory>>
    
    init {
        val calculationHistoryDao = CalculatorDatabase.getDatabase(application).calculationHistoryDao()
        repository = CalculatorRepository(calculationHistoryDao)
        allCalculations = repository.allCalculations
    }
    
    fun addCalculationToHistory(expression: String, result: Double, timestamp: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val calculationHistory = CalculationHistory(
                expression = expression,
                result = result,
                timestamp = timestamp
            )
            repository.insert(calculationHistory)
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearHistory()
        }
    }
}
