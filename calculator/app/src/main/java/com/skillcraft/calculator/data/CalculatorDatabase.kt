package com.skillcraft.calculator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CalculationHistory::class], version = 1, exportSchema = false)
abstract class CalculatorDatabase : RoomDatabase() {
    
    abstract fun calculationHistoryDao(): CalculationHistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: CalculatorDatabase? = null
        
        fun getDatabase(context: Context): CalculatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalculatorDatabase::class.java,
                    "calculator_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
