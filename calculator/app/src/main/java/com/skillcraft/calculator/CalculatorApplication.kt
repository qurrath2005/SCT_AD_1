package com.skillcraft.calculator

import android.app.Application
import com.skillcraft.calculator.data.CalculatorDatabase

class CalculatorApplication : Application() {
    
    val database by lazy { CalculatorDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-wide components here
    }
}
