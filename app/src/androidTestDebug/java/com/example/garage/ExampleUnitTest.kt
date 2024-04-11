package com.example.garage

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.garage.database.AppDatabase
import com.example.garage.database.Car
import com.example.garage.database.CarDao
import com.example.garage.viewModel.CarViewModel
import com.example.garage.viewModel.CarViewModelFactory
import org.hamcrest.CoreMatchers.not
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4 ::class)
class ExampleUnitTest {

    private lateinit var carDao: CarDao
    private lateinit var database: AppDatabase
    private lateinit var application: Application
    private lateinit var viewModel: CarViewModel

    val car = Car(1 ,"Model",  2016, "Brand", "empty", 1600, "Plate", "GAS",  10000.toDouble())
    val editedCar = Car(1 ,"ModelEdit",  2017, "BrandEdit", "empty", 1800, "PlateEdit", "GAS",  20000.toDouble())

    @Before
    fun setup()
    {
        application = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java).build()
        carDao = database.carDao()
        val factory = CarViewModelFactory(application, carDao)
        viewModel = ViewModelProvider(ViewModelStore(), factory)[CarViewModel::class.java]

    }
    @Test
    fun checkInsertedCar(){
        viewModel.addCar(car.brand,car.model,car.logo,car.year,car.displacement,car.supply,car.plate,car.km)
        Thread.sleep(1000)
        viewModel.carsList.value?.contains(car)?.let { Assert.assertTrue(it) }
    }

    @Test
    fun checkEditedCar(){
        viewModel.addCar(car.brand,car.model,car.logo,car.year,car.displacement,car.supply,car.plate,car.km)
        Thread.sleep(1000)
        viewModel.editCar(editedCar)
        Thread.sleep(1000)
        viewModel.carsList.value?.contains(editedCar)?.let { Assert.assertTrue(it) }
    }

    @Test
    fun checkDeletedCar(){
        viewModel.addCar(car.brand,car.model,car.logo,car.year,car.displacement,car.supply,car.plate,car.km)
        Thread.sleep(1000)
        viewModel.deleteCar()
        Thread.sleep(1000)
        viewModel.carsList.value?.contains(car)?.let { not(Assert.assertTrue(it)) }
    }
}