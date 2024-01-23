package com.example.garage.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.example.garage.R

@BindingAdapter("carBrand")
fun loadBrand(textView: TextView, data: String?) {
    textView.text = data ?: "???"
}

@BindingAdapter("carModel")
fun loadModel(textView: TextView, data: String?) {
    textView.text = data ?: "???"
}

@BindingAdapter("carKm")
fun loadKm(textView: TextView, data: Double?) {
    textView.text = data.toString() + " Km"
}

@BindingAdapter("carYear")
fun loadYear(textView: TextView, data: Int?) {
    textView.text = data.toString()
}

@BindingAdapter("carDisplacement")
fun loadDisplacement(textView: TextView, data: Int?) {
    textView.text = data.toString()
}

@BindingAdapter("carSupply")
fun loadSupply(textView: TextView, data: String?) {
    textView.text = data ?: "???"
}

@BindingAdapter("carPlate")
fun loadPlate(textView: TextView, data: String?) {
    textView.text = data ?: "???"
}

@BindingAdapter("carImage")
fun loadImage(imageView: ImageView, data: String?) {
    imageView.load(data){
        error(R.drawable.ic_car)
    }
}