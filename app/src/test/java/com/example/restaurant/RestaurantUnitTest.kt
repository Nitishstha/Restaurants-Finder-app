package com.example.kotlin

//import com.example.kotlin.model.RestaurantModel
//import com.example.kotlin.repository.RestaurantRepoImpl
//import com.example.kotlin.viewmodel.RestaurantViewModel // Fixed name
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import com.example.restaurant.repo.RestaurantRepoImpl
import com.example.restaurant.Model.RestaurantModel
import com.example.restaurant.viewmodel.RestaurantViewModel
class RestaurantsViewModelTest {

    @Test
    fun testAddRestaurantSuccess() {
        val mockRepo = mock(RestaurantRepoImpl::class.java)
        val viewModel = RestaurantViewModel(mockRepo) // Fixed name
        // Fixed: Use 'location' and 'cuisine' to match your Model
        val dummyRestaurant = RestaurantModel(name = "sndar", location = "setopul", cuisine = "nepali")

        whenever(mockRepo.addRestaurant(any(), any())).thenAnswer {
            val callback = it.arguments[1] as (Boolean, String) -> Unit
            callback(true, "Success")
        }

        viewModel.addRestaurant(dummyRestaurant) { success, message ->
            assertTrue(success)
            assertEquals("Success", message)
        }
    }

    @Test
    fun testAddRestaurantFailure() {
        val mockRepo = mock(RestaurantRepoImpl::class.java)
        val viewModel = RestaurantViewModel(mockRepo) // Fixed name
        val dummyRestaurant = RestaurantModel(name = "sndar", location = "setopul", cuisine = "nepali")

        whenever(mockRepo.addRestaurant(any(), any())).thenAnswer {
            val callback = it.arguments[1] as (Boolean, String) -> Unit
            callback(false, "Database Error")
        }

        viewModel.addRestaurant(dummyRestaurant) { success, message ->
            assertFalse(success)
            assertEquals("Database Error", message)
        }
    }
}