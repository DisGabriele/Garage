package com.example.garage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.garage.databinding.AutocompleteBinding

class CarModelAdapter() : ListAdapter<String, CarModelAdapter.CarModelViewHolder>(DiffCallback) {

    class CarModelViewHolder(
        private var binding: AutocompleteBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(model: String) {
            binding.textView.text = model
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarModelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CarModelViewHolder(
            AutocompleteBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CarModelViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
    }
}