package com.example.garage

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.garage.database.AppDatabase
import com.example.garage.database.CarDao
import com.example.garage.viewModel.CarViewModel
import com.example.garage.viewModel.CarViewModelFactory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var carDao: CarDao
    private lateinit var database: AppDatabase
    private lateinit var application: Application
    private lateinit var viewModel: CarViewModel

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java).build()
        carDao = database.carDao()
        viewModel = ViewModelProvider(ViewModelStore(), CarViewModelFactory(application, carDao))[CarViewModel::class.java]

        launchActivity<MainActivity>()
    }

   @Test
   fun addCar(){
       Espresso.onView(withId(R.id.floatButton)).perform(ViewActions.click())
       Espresso.onView(withId(R.id.add)).perform(ViewActions.click())

   }


}