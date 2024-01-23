package com.example.garage.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.garage.adapters.CarListAdapter
import com.example.garage.database.BaseApplication
import com.example.garage.database.Car
import com.example.garage.databinding.FragmentCarListBinding
import com.example.garage.viewModel.CarViewModel
import com.example.garage.viewModel.CarViewModelFactory
import kotlinx.coroutines.launch

class CarListFragment : Fragment() {

    private val navigationArgs: CarListFragmentArgs by navArgs()

    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!
    private lateinit var carList: List<Car>

    class CarListOnBackPressedCallback(
        private val slidingPaneLayout: SlidingPaneLayout
    ): OnBackPressedCallback(slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen),
        SlidingPaneLayout.PanelSlideListener{

        init {
            slidingPaneLayout.addPanelSlideListener(this)
        }

        override fun handleOnBackPressed() {
            slidingPaneLayout.closePane()
        }

        override fun onPanelSlide(panel: View, slideOffset: Float) {
        }

        override fun onPanelOpened(panel: View) {
            isEnabled = true
        }

        override fun onPanelClosed(panel: View) {
            isEnabled = false
        }
    }

    private val carViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(activity?.application!!,
            (activity?.application!! as BaseApplication).database
                .carDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply{
            lifecycleOwner = viewLifecycleOwner
            listFragment = this@CarListFragment
            viewModel = carViewModel
            binding.isOpen = false
        }

        val adapter = CarListAdapter(carViewModel) { car ->
            carViewModel.updateCurrentCar(car)
            binding.carsPanel.openPane()
            binding.isOpen = false
        }

        binding.carsPanel.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        binding.carsList.adapter = adapter

        lifecycle.coroutineScope.launch {
            carViewModel.collectCars().collect(){
                carList = it
                adapter.submitList(it)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            CarListOnBackPressedCallback(binding.carsPanel)
        )

        binding?.floatButton?.setOnClickListener {
            binding?.isOpen = !binding?.isOpen!!
        }

        binding.addCar.setOnClickListener{
            binding.isOpen = false
            val action = CarListFragmentDirections
                .actionCarListFragmentToAddCarFragment(false)
            findNavController().navigate(action)
        }


        carViewModel.deletion.observe(viewLifecycleOwner){
            if(it == true){
                binding.carsPanel.closePane()
                lifecycle.coroutineScope.launch {
                    carViewModel.deleteCar()
                }
                carViewModel.setDeletion(false)
            }
        }


        binding.searchBtn.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean{

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.submitList(
                    carList.filter {car ->
                        car.brand.contains(newText.toString(),true) ||
                                car.model.contains(newText.toString(),true) ||
                                car.plate.contains(newText.toString(),true)
                    }
                )
                return true
            }

        })

        binding.searchBtn.setOnCloseListener {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            adapter.submitList(carList)
            true
        }
    }

}