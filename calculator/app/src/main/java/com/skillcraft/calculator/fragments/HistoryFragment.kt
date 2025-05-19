package com.skillcraft.calculator.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skillcraft.calculator.R
import com.skillcraft.calculator.adapters.HistoryAdapter
import com.skillcraft.calculator.databinding.FragmentHistoryBinding
import com.skillcraft.calculator.viewmodels.CalculatorViewModel
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CalculatorViewModel
    private lateinit var adapter: HistoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]
        
        setupRecyclerView()
        setupButtons()
        observeHistory()
    }
    
    private fun setupRecyclerView() {
        adapter = HistoryAdapter { expression, result ->
            // Handle click on history item (copy to clipboard or return to calculator)
            val bundle = Bundle().apply {
                putString("expression", expression)
                putDouble("result", result)
            }
            
            val calculatorFragment = CalculatorFragment().apply {
                arguments = bundle
            }
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, calculatorFragment)
                .commit()
        }
        
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }
    
    private fun setupButtons() {
        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
        
        binding.btnExport.setOnClickListener {
            exportHistory()
        }
    }
    
    private fun observeHistory() {
        viewModel.allCalculations.observe(viewLifecycleOwner) { calculations ->
            adapter.submitList(calculations)
            
            if (calculations.isEmpty()) {
                binding.tvEmptyHistory.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvEmptyHistory.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
            }
        }
    }
    
    private fun exportHistory() {
        val calculations = viewModel.allCalculations.value ?: return
        
        if (calculations.isEmpty()) {
            Toast.makeText(requireContext(), "No history to export", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val fileName = "calculator_history_$timestamp.csv"
            
            val file = File(requireContext().getExternalFilesDir(null), fileName)
            val writer = FileWriter(file)
            
            // Write header
            writer.append("Expression,Result,Timestamp\n")
            
            // Write data
            calculations.forEach { calculation ->
                writer.append("${calculation.expression},${calculation.result},${calculation.timestamp}\n")
            }
            
            writer.flush()
            writer.close()
            
            // Share the file
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Calculator History")
                putExtra(Intent.EXTRA_TEXT, "Exported calculator history")
                putExtra(Intent.EXTRA_STREAM, android.net.Uri.parse("file://${file.absolutePath}"))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(intent, "Share History"))
            
            Toast.makeText(requireContext(), R.string.history_export_success, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.history_export_error, Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
