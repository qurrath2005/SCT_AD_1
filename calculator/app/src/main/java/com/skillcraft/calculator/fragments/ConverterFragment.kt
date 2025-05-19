package com.skillcraft.calculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.skillcraft.calculator.databinding.FragmentConverterBinding
import com.skillcraft.calculator.utils.ConverterUtil

class ConverterFragment : Fragment() {

    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!
    
    private val converterUtil = ConverterUtil()
    private var currentConverterType = ConverterType.CURRENCY
    
    enum class ConverterType {
        CURRENCY, LENGTH, WEIGHT, TEMPERATURE
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupTabLayout()
        setupSpinners()
        setupConvertButton()
    }
    
    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> setConverterType(ConverterType.CURRENCY)
                    1 -> setConverterType(ConverterType.LENGTH)
                    2 -> setConverterType(ConverterType.WEIGHT)
                    3 -> setConverterType(ConverterType.TEMPERATURE)
                }
            }
            
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }
    
    private fun setConverterType(type: ConverterType) {
        currentConverterType = type
        updateSpinners()
    }
    
    private fun setupSpinners() {
        updateSpinners()
    }
    
    private fun updateSpinners() {
        val units = when (currentConverterType) {
            ConverterType.CURRENCY -> converterUtil.getCurrencyUnits()
            ConverterType.LENGTH -> converterUtil.getLengthUnits()
            ConverterType.WEIGHT -> converterUtil.getWeightUnits()
            ConverterType.TEMPERATURE -> converterUtil.getTemperatureUnits()
        }
        
        val fromAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, units)
        val toAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, units)
        
        (binding.spinnerFrom as? AutoCompleteTextView)?.setAdapter(fromAdapter)
        (binding.spinnerTo as? AutoCompleteTextView)?.setAdapter(toAdapter)
        
        // Set default values
        if (units.isNotEmpty()) {
            (binding.spinnerFrom as? AutoCompleteTextView)?.setText(units[0], false)
            (binding.spinnerTo as? AutoCompleteTextView)?.setText(units.getOrElse(1) { units[0] }, false)
        }
    }
    
    private fun setupConvertButton() {
        binding.btnConvert.setOnClickListener {
            convert()
        }
    }
    
    private fun convert() {
        val valueStr = binding.etValue.text.toString()
        if (valueStr.isEmpty()) {
            binding.tilValue.error = "Please enter a value"
            return
        }
        
        val value = valueStr.toDoubleOrNull()
        if (value == null) {
            binding.tilValue.error = "Invalid number"
            return
        }
        
        binding.tilValue.error = null
        
        val fromUnit = (binding.spinnerFrom as? AutoCompleteTextView)?.text.toString()
        val toUnit = (binding.spinnerTo as? AutoCompleteTextView)?.text.toString()
        
        if (fromUnit.isEmpty() || toUnit.isEmpty()) {
            return
        }
        
        val result = when (currentConverterType) {
            ConverterType.CURRENCY -> converterUtil.convertCurrency(value, fromUnit, toUnit)
            ConverterType.LENGTH -> converterUtil.convertLength(value, fromUnit, toUnit)
            ConverterType.WEIGHT -> converterUtil.convertWeight(value, fromUnit, toUnit)
            ConverterType.TEMPERATURE -> converterUtil.convertTemperature(value, fromUnit, toUnit)
        }
        
        binding.tvResultValue.text = "$value $fromUnit = $result $toUnit"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
