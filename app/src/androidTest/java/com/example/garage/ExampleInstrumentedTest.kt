package com.example.garage

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.garage.database.AppDatabase
import com.example.garage.database.CarDao
import com.example.garage.viewModel.CarViewModel
import com.example.garage.viewModel.CarViewModelFactory
import org.hamcrest.Matchers.not
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    @Before
    fun setup() {
        launchActivity<MainActivity>()

        Espresso.onView(withId(R.id.floatButton)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.add_car)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.brand_input))
            .perform(ViewActions.replaceText("Brand"))

        Espresso.onView(withId(R.id.model_input))
            .perform(ViewActions.replaceText("Model"))

        Espresso.onView(withId(R.id.km_input))
            .perform(ViewActions.replaceText("10000"))

        Espresso.onView(withId(R.id.year_input))
            .perform(ViewActions.replaceText("2016"))

        Espresso.onView(withId(R.id.displacement_input))
            .perform(ViewActions.replaceText("1600"))

        Espresso.onView(withId(R.id.plate_input))
            .perform(ViewActions.replaceText("Plate"))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(ViewActions.click())

    }

   @Test
   fun newCarIsDisplayed(){
       Espresso.onView(ViewMatchers.withText("Brand"))
           .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

       Espresso.onView(ViewMatchers.withText("Model"))
           .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

       Espresso.onView(ViewMatchers.withText("10000.0 Km"))
           .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

       Espresso.onView(ViewMatchers.withText("2016"))
           .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

       Espresso.onView(ViewMatchers.withText("1600"))
           .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

       Espresso.onView(ViewMatchers.withText("Plate"))
           .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
   }

    @Test
    fun editCar(){
        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.edit_car_btn_detail))
            .perform(click())

        Espresso.onView(withId(R.id.brand_input))
            .perform(ViewActions.replaceText("BrandEdit"))

        Espresso.onView(withId(R.id.model_input))
            .perform(ViewActions.replaceText("ModelEdit"))

        Espresso.onView(withId(R.id.km_input))
            .perform(ViewActions.replaceText("20000"))

        Espresso.onView(withId(R.id.year_input))
            .perform(ViewActions.replaceText("2024"))

        Espresso.onView(withId(R.id.displacement_input))
            .perform(ViewActions.replaceText("1800"))

        Espresso.onView(withId(R.id.plate_input))
            .perform(ViewActions.replaceText("PlateEdit"))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(click())


        Thread.sleep(500)

        Espresso.onView(ViewMatchers.withText("BrandEdit"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("ModelEdit"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("20000.0 Km"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("2024"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("1800"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("PlateEdit"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun deleteCar(){

        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.delete_btn))
            .perform(ViewActions.click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withText("Si"))
            .perform(ViewActions.click())

        Thread.sleep(500)

        Espresso.onView(ViewMatchers.withText("Brand"))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun wrongYear(){
        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.edit_car_btn_detail))
            .perform(click())

        Espresso.onView(withId(R.id.year_input))
            .perform(ViewActions.replaceText("${Calendar.getInstance()
                .get(Calendar.YEAR) + 1}"))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withId(R.id.save_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(withId(R.id.year_input))
            .perform(ViewActions.replaceText("1800"))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withId(R.id.save_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun emptyBrand(){
        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.edit_car_btn_detail))
            .perform(click())

        Espresso.onView(withId(R.id.brand_input))
            .perform(ViewActions.replaceText(""))

        ViewActions.closeSoftKeyboard()

            Espresso.onView(withId(R.id.save_btn))
                .perform(click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withId(R.id.save_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyModel(){
        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.edit_car_btn_detail))
            .perform(click())

        Espresso.onView(withId(R.id.model_input))
            .perform(ViewActions.replaceText(""))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withId(R.id.save_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyKm(){
        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.edit_car_btn_detail))
            .perform(click())

        Espresso.onView(withId(R.id.km_input))
            .perform(ViewActions.replaceText(""))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withId(R.id.save_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyYear(){
        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.edit_car_btn_detail))
            .perform(click())

        Espresso.onView(withId(R.id.year_input))
            .perform(ViewActions.replaceText(""))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withId(R.id.save_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyDisplacement(){
        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.edit_car_btn_detail))
            .perform(click())

        Espresso.onView(withId(R.id.displacement_input))
            .perform(ViewActions.replaceText(""))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withId(R.id.save_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyPlate(){
        Espresso.onView(withId(R.id.floatButtonDetail))
            .perform(scrollTo())
            .perform(click())

        Espresso.onView(withId(R.id.edit_car_btn_detail))
            .perform(click())

        Espresso.onView(withId(R.id.plate_input))
            .perform(ViewActions.replaceText(""))

        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.save_btn))
            .perform(click())

        Thread.sleep(300)

        Espresso.onView(ViewMatchers.withId(R.id.save_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }



}