package com.example.garage.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.garage.R
import com.example.garage.database.Car
import com.example.garage.database.CarDao
import com.example.garage.network.GarageApi
import com.example.garage.network.Logo
import com.example.garage.workers.CarServiceWorker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class CarViewModel(application: Application, private val carDao: CarDao): ViewModel() {

    private var _deletion: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val deletion: LiveData<Boolean> get() = _deletion

    private var _currentCar: MutableLiveData<Car> = MutableLiveData<Car>()
    val currentCar: LiveData<Car> get() = _currentCar

    private var _currentCarPosition: MutableLiveData<Int> = MutableLiveData<Int>()
    val currentCarPosition: LiveData<Int> get() = _currentCarPosition

    private var _edit: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val edit: LiveData<Boolean> get() = _edit

    private val _logoList = MutableLiveData<List<Logo>>(listOf())
    val logoList: LiveData<List<Logo>> get() = _logoList


    val workManager = WorkManager.getInstance(application.applicationContext)

    private fun getLogoList(){
        viewModelScope.launch {
            while(_logoList.value!!.isEmpty()) {
                try {
                    _logoList.value = GarageApi.retrofitService.getLogos()

                } catch (e: Exception) {
                    _logoList.value = listOf()
                }
            }
        }
    }

    fun setEdit(value: Boolean){
        _edit.value = value
    }

    fun setCarPosition(position: Int){
        _currentCarPosition.value = position
    }


    fun setDeletion(value: Boolean){
        _deletion.value = value
    }

    fun updateCurrentCar(car: Car){
        _currentCar.value = car
    }

    fun deleteCar() {
        viewModelScope.launch(Dispatchers.IO) {
            currentCar.value?.let { carDao.delete(it)
            }
        }
    }



    fun collectCars(): Flow<List<Car>> = carDao.getAll().flowOn(Dispatchers.IO)

    private fun insertCar(item: Car) {
        viewModelScope.launch {
            carDao.insert(item)
        }
    }
    fun addCar(
        model: String,
        brand: String,
        logo: String,
        year: Int,
        displacement: Int,
        supply: String,
        plate: String,
        km: Double

    ) {
        val car = Car(
            model = model,
            brand = brand,
            logo = logo,
            year = year,
            displacement = displacement,
            supply = supply,
            plate = plate,
            km = km
        )
        insertCar(car)
    }

    private fun updateCar(item: Car) {
        viewModelScope.launch(Dispatchers.IO) {
            carDao.update(item)
        }
    }

    fun editCar(car: Car
    ) {
        updateCurrentCar(car)
        updateCar(car)
    }

    fun showDialog(context: Context): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setMessage(
                context.getString(
                    R.string.delete_dialog,
                    currentCar.value?.brand,
                    currentCar.value?.model
                )
            )
            .setCancelable(false)
            .setNegativeButton("No") { _, _ ->
            }
    }

    init{
        getLogoList()
    }

    internal fun carServiceReminder(
        carName: String
    ) {
        val data = Data.Builder()
        data.putString(CarServiceWorker.nameKey, carName)

        val notification = OneTimeWorkRequestBuilder<CarServiceWorker>()
            .setInputData(data.build())
            .addTag("CarService")
            .build()

        workManager.enqueueUniqueWork(
            carName,
            ExistingWorkPolicy.KEEP,
            notification
        )

    }
}


class CarViewModelFactory(
    private val application: Application,
    private val carDao: CarDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarViewModel(application,carDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
