package com.example.jetpackimplementations.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.jetpackimplementations.R
import com.example.jetpackimplementations.model.Crypto
import com.example.jetpackimplementations.model.CryptoResponse
import com.example.jetpackimplementations.service.CryptoAPI
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Headers.Companion.toHeaders
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

//    private val BASE_URL = "https://pro-api.coinmarketcap.com/v1/"
//    private var cryptoList:ArrayList<Crypto>? = null


    private lateinit var navigationController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        loadData()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //Inflater
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.art_menu,menu)

        // Initialize NavController and setup ActionBar
        navigationController = Navigation.findNavController(this, R.id.fragmentContainerView)
        NavigationUI.setupActionBarWithNavController(this, navigationController)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.fragmentContainerView)
        return navController.navigateUp()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val navController = this.findNavController(R.id.fragmentContainerView)

        if (item.itemId == R.id.add_art_item ) {
            val currentDestination = navController.currentDestination
            if (currentDestination?.id == R.id.secondFragment2) {
                // Navigate back to the FirstFragment
                navController.popBackStack(R.id.firstFragment, false)
            }

            val action = FirstFragmentDirections.firstSecondFrg("new",0)
            navController.navigate(action) // Use the existing navController
            return true

        }

        if (item.itemId == R.id.go_to_cryptos ) {
            val currentDestination = navController.currentDestination
//            if (currentDestination?.id == R.id.secondFragment2) {
//                // Navigate back to the FirstFragment
//                navController.popBackStack(R.id.firstFragment, false)
//            }

            val action = FirstFragmentDirections.firstToCrypto()
            navController.navigate(action) // Use the existing navController
            return true
        }


        return super.onOptionsItemSelected(item)
    }

//    private fun loadData(){
//
//        val contentType = "application/json".toMediaType()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(Json.asConverterFactory(contentType))
//            .build()
//
//        val service = retrofit.create(CryptoAPI::class.java)
//        val call = service.getData()
//
//        call.enqueue (object: Callback<CryptoResponse> {
//            override fun onResponse(call: Call<CryptoResponse>, response: Response<CryptoResponse>) {
//               if (response.isSuccessful) {
//                   val cryptoResponse = response.body()
//                   var count = 0
//                   try {
//                        cryptoList = cryptoResponse?.data as ArrayList<Crypto>
//
//                       for (crypto in cryptoList!!) {
//                           println(crypto)
//                           count = count+1
//                       }
//                       println(count)
//                   } catch (e: Exception) {
//                       println("JSON deserialization error: ${e.message}")
//                   }
//               }
//            }
//
//            override fun onFailure(call: Call<CryptoResponse>, t: Throwable) {
//                println(t)
//            }
//
//        })
//
//
//    }

}