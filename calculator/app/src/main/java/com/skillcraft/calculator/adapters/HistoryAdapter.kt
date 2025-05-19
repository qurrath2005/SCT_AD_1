package com.skillcraft.calculator.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skillcraft.calculator.data.CalculationHistory
import com.skillcraft.calculator.databinding.ItemHistoryBinding

class HistoryAdapter(private val onItemClick: (String, Double) -> Unit) :
    ListAdapter<CalculationHistory, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onItemClick(item.expression, item.result)
                }
            }
            
            binding.btnCopy.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    copyToClipboard(item)
                }
            }
        }
        
        fun bind(history: CalculationHistory) {
            binding.tvHistoryExpression.text = history.expression
            binding.tvHistoryResult.text = history.result.toString()
            binding.tvHistoryTimestamp.text = history.timestamp
        }
        
        private fun copyToClipboard(history: CalculationHistory) {
            val clipboardManager = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(
                "Calculation",
                "${history.expression} = ${history.result}"
            )
            clipboardManager.setPrimaryClip(clipData)
            
            Toast.makeText(binding.root.context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    
    class HistoryDiffCallback : DiffUtil.ItemCallback<CalculationHistory>() {
        override fun areItemsTheSame(oldItem: CalculationHistory, newItem: CalculationHistory): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: CalculationHistory, newItem: CalculationHistory): Boolean {
            return oldItem == newItem
        }
    }
}
