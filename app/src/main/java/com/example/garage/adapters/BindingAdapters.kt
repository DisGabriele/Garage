package com.example.garage.adapters

import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.example.garage.R

@BindingAdapter("carBrand")
fun loadBrand(textView: TextView, data: String?) {
    textView.text = data ?: "Brand"

}

@BindingAdapter("carModel")
fun loadModel(textView: TextView, data: String?) {
    textView.text = data ?: "Model"
}

@BindingAdapter("carKm")
fun loadKm(textView: TextView, data: Double?) {
    textView.text = data.toString() + " Km" ?: "Km"
}

@BindingAdapter("carYear")
fun loadYear(textView: TextView, data: Int?) {
    textView.text = data.toString() ?: "Year"
}

@BindingAdapter("carDisplacement")
fun loadDisplacement(textView: TextView, data: Int?) {
    textView.text = data.toString() ?: "Displacement"
}

@BindingAdapter("carSupply")
fun loadSupply(textView: TextView, data: String?) {
    textView.text = data ?: "Supply"
}

@BindingAdapter("carPlate")
fun loadPlate(textView: TextView, data: String?) {
    textView.text = data ?: "Plate"
}

@BindingAdapter("carLogo")
fun loadLogo(imageView: ImageView, data: String?) {
    imageView.load(data){
        error(R.drawable.ic_car)
    }
}

@BindingAdapter("carImage")
fun loadImage(imageView: ImageView, data: ByteArray?){
    val bmp = data?.let { BitmapFactory.decodeByteArray(data, 0, it.size) }
    imageView.setImageBitmap(bmp)
}
