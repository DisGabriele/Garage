package com.example.garage.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.garage.R
import com.example.garage.database.BaseApplication
import com.example.garage.database.Car
import com.example.garage.databinding.FragmentAddCarBinding
import com.example.garage.viewModel.CarViewModel
import com.example.garage.viewModel.CarViewModelFactory
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.Locale


enum class ValidationError {
    EMPTY, KM, YEAR, GOOD, PlATE
}

class AddCarFragment : Fragment() {

    private var status = ValidationError.GOOD

    private val navigationArgs: AddCarFragmentArgs by navArgs()

    private var _binding: FragmentAddCarBinding? = null
    private val binding get() = _binding!!

    private val changeImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.carModelImage.setImageURI(it.data?.data)
        }
    }

    private val carViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(
            activity?.application!!,
            (activity?.application!! as BaseApplication).database
                .carDao()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            addFragment = this@AddCarFragment
            viewModel = carViewModel
        }

        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        val logoNames = binding.viewModel!!.logoList.value!!.map { logo ->
            logo.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        }.sorted()

        val brandAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.autocomplete,
            logoNames
        )

        val supplyList = mutableListOf("")
        supplyList.clear()
        resources.getStringArray(R.array.supply_list).forEach { supply ->
            supplyList += supply
        }


        val supplyAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.autocomplete,
            supplyList
        )

        if (resources.configuration.screenWidthDp > 600 || resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.modelInput.setOnFocusChangeListener { model, _ ->
                if (model.hasFocus()) {
                    binding.modelInput.showDropDown()
                } else {
                    binding.modelInput.dismissDropDown()
                }
            }
        }

        binding.brandInput.setAdapter(brandAdapter)
        binding.brandInput.doAfterTextChanged { brand ->
            if (logoNames.contains(brand.toString())) {
                binding.modelInput.setAdapter(
                    ArrayAdapter(
                        requireActivity(),
                        R.layout.autocomplete,
                        binding.viewModel?.logoList?.value?.filter { logo -> logo.name == brand.toString() }
                            ?.get(0)?.models ?: listOf(")")
                    )
                )
            }
        }


        binding.supplyInput.adapter = supplyAdapter

        binding.editImageBtn.setOnClickListener{
            addCarImage()
        }

        if (!navigationArgs.addEdit) {
            binding.toolbar.title = resources.getString(R.string.add_a_car)
            binding.supplyInput.setSelection(0)

            binding.saveBtn.setOnClickListener {
                addCar()
            }
        } else {
            binding.toolbar.title = resources.getString(R.string.edit)
            binding.brandInput.setText(carViewModel.currentCar.value?.brand)
            binding.modelInput.setText(carViewModel.currentCar.value?.model)
            binding.kmInput.setText(carViewModel.currentCar.value?.km.toString())
            binding.yearInput.setText(carViewModel.currentCar.value?.year.toString())
            binding.displacementInput.setText(carViewModel.currentCar.value?.displacement.toString())
            binding.supplyInput.setSelection(supplyList.indexOf(carViewModel.currentCar.value?.supply))
            binding.plateInput.setText(carViewModel.currentCar.value?.plate)

            binding.saveBtn.setOnClickListener {
                updateCar()
            }
        }

    }


    private fun addCarImage(){
        val pickImg = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        changeImage.launch(pickImg)
    }

    private fun Bitmap.toByteArray(): ByteArray{
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()

    }


    private fun isValidEntry() {
        if (binding.brandInput.text.isNullOrEmpty() ||
            binding.modelInput.text.isNullOrEmpty() ||
            binding.kmInput.text.toString().isEmpty() ||
            binding.yearInput.text.isNullOrEmpty() ||
            binding.displacementInput.text.isNullOrEmpty() ||
            binding.plateInput.text.isNullOrEmpty()
        ) {
            status = ValidationError.EMPTY
            Toast.makeText(context, resources.getString(R.string.error_message), Toast.LENGTH_SHORT)
                .show()
        } else if (binding.yearInput.text.toString().toInt() > Calendar.getInstance()
                .get(Calendar.YEAR) || binding.yearInput.text.toString().toInt() < 1886
        ) {
            status = ValidationError.YEAR
            Toast.makeText(
                context,
                resources.getString(R.string.year_error_message),
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.kmInput.text.toString().toDouble() < 0) {
            status = ValidationError.KM
            Toast.makeText(
                context,
                resources.getString(R.string.negative_km_error_message),
                Toast.LENGTH_SHORT
            ).show()
        } else if (carViewModel.carsList.value?.isNotEmpty() == true) {
            var carPlateList = carViewModel.carsList.value!!.filter { car ->
                car.plate.equals(
                    binding.plateInput.text.toString(),
                    true
                )
            }
            if (!navigationArgs.addEdit) {
                if (carPlateList.isNotEmpty()) {
                    status = ValidationError.PlATE
                    Toast.makeText(
                        context,
                        resources.getString(R.string.plate_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    status = ValidationError.GOOD
                }
            } else {
                carPlateList = carPlateList.minus(carViewModel.currentCar.value!!)
                if (carPlateList.isNotEmpty()) {
                    status = ValidationError.PlATE
                    Toast.makeText(
                        context,
                        resources.getString(R.string.plate_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    status = ValidationError.GOOD
                }
            }
        } else {
            status = ValidationError.GOOD
        }
    }

    private fun addCar() {
        isValidEntry()
        if (status == ValidationError.GOOD) {
            var logo = "empty"
            val logos = binding.viewModel?.logoList?.value?.filter {
                it.name.equals(
                    binding.brandInput.text.toString(),
                    true
                )
            }
            if (logos?.isNotEmpty() == true) {
                logo = logos[0].logo
            }

            carViewModel.addCar(
                binding.modelInput.text.toString(),
                binding.brandInput.text.toString(),
                logo,
                binding.yearInput.text.toString().toInt(),
                (binding.carModelImage.drawable.toBitmap()).toByteArray(),
                binding.displacementInput.text.toString().toInt(),
                binding.supplyInput.selectedItem.toString(),
                binding.plateInput.text.toString(),
                binding.kmInput.text.toString().toDouble()
            )

            if (binding.kmInput.text.toString().toDouble() >= 10000) {
                carViewModel.carServiceReminder(binding.brandInput.text.toString() + " " + binding.modelInput.text.toString())
            }

            val action = AddCarFragmentDirections
                .actionAddCarFragmentToCarListFragment(true)
            findNavController().navigate(action)
        }

    }

    private fun updateCar() {
        isValidEntry()
        if (status == ValidationError.GOOD) {
            if (binding.kmInput.text.toString().toDouble() < carViewModel.currentCar.value!!.km) {
                status = ValidationError.KM
                Toast.makeText(
                    context,
                    resources.getString(R.string.less_km_error_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var logo = "empty"
                val logos = binding.viewModel?.logoList?.value?.filter {
                    it.name.equals(
                        binding.brandInput.text.toString(),
                        true
                    )
                }
                if (logos?.isNotEmpty() == true) {
                    logo = logos[0].logo
                }
                val updatedCar = Car(
                    carViewModel.currentCar.value!!.id,
                    binding.modelInput.text.toString(),
                    binding.yearInput.text.toString().toInt(),
                    binding.brandInput.text.toString(),
                    logo,
                    (binding.carModelImage.drawable.toBitmap()).toByteArray(),
                    binding.displacementInput.text.toString().toInt(),
                    binding.plateInput.text.toString(),
                    binding.supplyInput.selectedItem.toString(),
                    binding.kmInput.text.toString().toDouble()
                )

                if (updatedCar.km.minus(carViewModel.currentCar.value!!.km) >= 10000) {
                    carViewModel.carServiceReminder(updatedCar.brand + " " + updatedCar.model)
                }

                carViewModel.editCar(updatedCar)

                val action = AddCarFragmentDirections
                    .actionAddCarFragmentToCarListFragment(true)
                findNavController().navigate(action)
            }
        }
    }
}
