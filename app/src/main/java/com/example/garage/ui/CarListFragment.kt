package com.example.garage.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.garage.R
import com.example.garage.adapters.CarListAdapter
import com.example.garage.database.BaseApplication
import com.example.garage.databinding.FragmentCarListBinding
import com.example.garage.viewModel.CarViewModel
import com.example.garage.viewModel.CarViewModelFactory
import kotlinx.coroutines.launch

class CarListFragment : Fragment() {

    private val navigationArgs: CarListFragmentArgs by navArgs()

    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!

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

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            listFragment = this@CarListFragment
            viewModel = carViewModel
            binding.isOpen = false
        }

        if(resources.configuration.screenWidthDp > 600){
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
                        carViewModel.deleteCar()
                    }
                    .show()
            }

            binding.removeCarText?.setOnClickListener{
                binding.deleteBtn?.performClick()
            }
        }


        val adapter = CarListAdapter(carViewModel) { car ->
            carViewModel.updateCurrentCar(car)
            binding.carsPanel.openPane()
            binding.isOpen = false
        }

        binding.carsPanel.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        binding.carsList.adapter = adapter

        carViewModel.carsList.observe(viewLifecycleOwner){carList ->
            adapter.submitList(carList)
            if (carList.isNotEmpty()) {
                if(!carViewModel.currentCar.isInitialized){
                carViewModel.updateCurrentCar(carList[0])
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            CarListOnBackPressedCallback(binding.carsPanel)
        )

        binding?.floatButton?.setOnClickListener {
            binding?.isOpen = !binding?.isOpen!!
        }

        binding.addCar.setOnClickListener {
            binding.isOpen = false
            val action = CarListFragmentDirections
                .actionCarListFragmentToAddCarFragment(false)
            findNavController().navigate(action)
        }

        binding.addCarText.setOnClickListener{
            binding.addCar.performClick()
        }


        carViewModel.deletion.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.carsPanel.closePane()
                lifecycle.coroutineScope.launch {
                    carViewModel.deleteCar()
                }
                carViewModel.setDeletion(false)
            }
        }


        binding.searchBtn.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.submitList(
                    carViewModel.carsList.value?.filter { car ->
                                car.brand.startsWith(newText.toString(), true) ||
                                car.model.startsWith(newText.toString(), true)
                    }
                )
                return true
            }

        })

        binding.searchBtn.setOnCloseListener {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            adapter.submitList(carViewModel.carsList.value)
            true
        }
    }
}