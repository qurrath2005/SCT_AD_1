package com.skillcraft.calculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skillcraft.calculator.R
import com.skillcraft.calculator.databinding.FragmentCalculatorBinding
import com.skillcraft.calculator.utils.ExpressionEvaluator
import com.skillcraft.calculator.viewmodels.CalculatorViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CalculatorViewModel
    private val expressionEvaluator = ExpressionEvaluator()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]
        
        // Check if scientific mode is enabled from arguments
        arguments?.getBoolean("scientific_mode", false)?.let {
            toggleScientificMode(it)
        }
        
        setupNumberButtons()
        setupOperationButtons()
        setupFunctionButtons()
        setupScientificButtons()
    }
    
    private fun setupNumberButtons() {
        // Number buttons
        binding.btn0.setOnClickListener { appendToExpression("0") }
        binding.btn1.setOnClickListener { appendToExpression("1") }
        binding.btn2.setOnClickListener { appendToExpression("2") }
        binding.btn3.setOnClickListener { appendToExpression("3") }
        binding.btn4.setOnClickListener { appendToExpression("4") }
        binding.btn5.setOnClickListener { appendToExpression("5") }
        binding.btn6.setOnClickListener { appendToExpression("6") }
        binding.btn7.setOnClickListener { appendToExpression("7") }
        binding.btn8.setOnClickListener { appendToExpression("8") }
        binding.btn9.setOnClickListener { appendToExpression("9") }
        binding.btnDecimal.setOnClickListener { appendToExpression(".") }
    }
    
    private fun setupOperationButtons() {
        // Operation buttons
        binding.btnAdd.setOnClickListener { appendToExpression("+") }
        binding.btnSubtract.setOnClickListener { appendToExpression("-") }
        binding.btnMultiply.setOnClickListener { appendToExpression("×") }
        binding.btnDivide.setOnClickListener { appendToExpression("÷") }
        binding.btnParenthesisOpenBasic.setOnClickListener { appendToExpression("(") }
        binding.btnParenthesisCloseBasic.setOnClickListener { appendToExpression(")") }
    }
    
    private fun setupFunctionButtons() {
        // Function buttons
        binding.btnClear.setOnClickListener { clearLastCharacter() }
        binding.btnClearAll.setOnClickListener { clearExpression() }
        binding.btnEquals.setOnClickListener { calculateResult() }
    }
    
    private fun setupScientificButtons() {
        // Scientific buttons
        binding.btnSin.setOnClickListener { appendToExpression("sin(") }
        binding.btnCos.setOnClickListener { appendToExpression("cos(") }
        binding.btnTan.setOnClickListener { appendToExpression("tan(") }
        binding.btnLog.setOnClickListener { appendToExpression("log(") }
        binding.btnLn.setOnClickListener { appendToExpression("ln(") }
        binding.btnSqrt.setOnClickListener { appendToExpression("√(") }
        binding.btnPower.setOnClickListener { appendToExpression("^") }
        binding.btnPi.setOnClickListener { appendToExpression("π") }
        binding.btnE.setOnClickListener { appendToExpression("e") }
        binding.btnParenthesisOpen.setOnClickListener { appendToExpression("(") }
        binding.btnParenthesisClose.setOnClickListener { appendToExpression(")") }
        binding.btnPercent.setOnClickListener { appendToExpression("%") }
    }
    
    private fun appendToExpression(value: String) {
        val currentExpression = binding.tvExpression.text.toString()
        binding.tvExpression.text = currentExpression + value
    }
    
    private fun clearLastCharacter() {
        val currentExpression = binding.tvExpression.text.toString()
        if (currentExpression.isNotEmpty()) {
            binding.tvExpression.text = currentExpression.substring(0, currentExpression.length - 1)
        }
    }
    
    private fun clearExpression() {
        binding.tvExpression.text = ""
        binding.tvResult.text = ""
    }
    
    private fun calculateResult() {
        val expression = binding.tvExpression.text.toString()
        if (expression.isNotEmpty()) {
            try {
                val result = expressionEvaluator.evaluate(expression)
                binding.tvResult.text = result.toString()
                
                // Save to history
                saveToHistory(expression, result)
            } catch (e: Exception) {
                binding.tvResult.text = "Error"
            }
        }
    }
    
    private fun saveToHistory(expression: String, result: Double) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        
        viewModel.addCalculationToHistory(expression, result, timestamp)
    }
    
    fun toggleScientificMode(enabled: Boolean) {
        binding.scientificPanel.visibility = if (enabled) View.VISIBLE else View.GONE
    }
    
    fun processVoiceInput(input: String) {
        // Process voice input and convert to calculation
        try {
            // Clean up the input
            val cleanInput = input.lowercase()
                .replace("what's", "")
                .replace("what is", "")
                .replace("calculate", "")
                .replace("equals", "")
                .replace("times", "×")
                .replace("multiplied by", "×")
                .replace("plus", "+")
                .replace("minus", "-")
                .replace("divided by", "÷")
                .trim()
            
            binding.tvExpression.text = cleanInput
            calculateResult()
        } catch (e: Exception) {
            binding.tvResult.text = "Could not process voice input"
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
