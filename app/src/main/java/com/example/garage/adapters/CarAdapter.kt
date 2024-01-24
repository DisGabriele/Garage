package com.example.garage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.garage.R
import com.example.garage.database.Car
import com.example.garage.databinding.ItemListBinding
import com.example.garage.viewModel.CarViewModel

class CarListAdapter(
    private val carViewModel: CarViewModel,
    private val clickListener: (Car) -> Unit
) : ListAdapter<Car, CarListAdapter.CarViewHolder>(DiffCallback) {

    class CarViewHolder(
        private var binding: ItemListBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            binding.model.text = car.brand + " " + car.model
            binding.year.text = car.year.toString()
            binding.carImage.load(car.logo){
                error(R.drawable.ic_block_car)
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean {
            return oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CarViewHolder(
            ItemListBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun getCurrentList(): MutableList<Car> {
        return super.getCurrentList()

        View.GONE
    }


    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = getItem(position)
        holder.itemView.setOnClickListener{
            carViewModel.setCarPosition(position)
            clickListener(car)
        }
        holder.bind(car)
    }
}