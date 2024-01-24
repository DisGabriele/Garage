package com.example.garage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.garage.R
import com.example.garage.database.BaseApplication
import com.example.garage.databinding.FragmentCarDetailBinding
import com.example.garage.databinding.FragmentCarDetailBindingLargeImpl
import com.example.garage.viewModel.CarViewModel
import com.example.garage.viewModel.CarViewModelFactory
import kotlin.reflect.jvm.internal.impl.types.checker.TypeRefinementSupport.Enabled

class CarDetailFragment() : Fragment() {

    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!


    private val carViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(activity?.application!!,
            (activity?.application!! as BaseApplication).database
                .carDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            detailFragment = this@CarDetailFragment
            viewModel = carViewModel
            isOpen = false
        }

        if(resources.configuration.screenWidthDp < 600){
            val toolbar = binding.toolbar
            (activity as AppCompatActivity).setSupportActionBar(toolbar)

            // Display the back arrow in the toolbar
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Handle the navigation action when the back arrow is clicked
            toolbar?.setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

        binding?.floatButton?.setOnClickListener {
            binding?.isOpen = !binding?.isOpen!!
        }

        binding.editCarBtn?.setOnClickListener{
            val action = CarListFragmentDirections
                .actionCarListFragmentToAddCarFragment(true)
            findNavController().navigate(action)
        }

        binding.editCarText?.setOnClickListener{
            binding.editCarBtn?.performClick()
        }

        binding.deleteBtn?.setOnClickListener{
            carViewModel.showDialog(it.context)
                .setPositiveButton(it.context.getString(R.string.confirm_dialog)) { _, _ ->
                    Toast.makeText(context,it.context.getString(R.string.delete_confirmed,carViewModel.currentCar.value?.brand,carViewModel.currentCar.value?.model),
                        Toast.LENGTH_SHORT).show()
                    carViewModel.setDeletion(true)
                    carViewModel.currentCar
                    binding.isOpen = false
                }
                .show()
        }

        binding.removeCarText?.setOnClickListener{
            binding.deleteBtn?.performClick()
        }
    }
}