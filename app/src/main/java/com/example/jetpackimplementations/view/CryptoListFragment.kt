package com.example.jetpackimplementations.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jetpackimplementations.adapter.CryptoRecyclerAdapter
import com.example.jetpackimplementations.databinding.FragmentCryptoListBinding
import com.example.jetpackimplementations.model.Crypto
import com.example.jetpackimplementations.model.CryptoResponse
import com.example.jetpackimplementations.service.CryptoAPI
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import kotlin.coroutines.coroutineContext

class CryptoListFragment : Fragment(),CryptoRecyclerAdapter.Listener {

    private var _binding: FragmentCryptoListBinding? = null;
    private val binding get() = _binding!!
    private val compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error : ${throwable.localizedMessage}")
    }


    private val BASE_URL = "https://pro-api.coinmarketcap.com/v1/"
    private var cryptoList:ArrayList<Crypto> = arrayListOf(Crypto.placeholder)

    private lateinit var cryptoAdapter: CryptoRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCryptoListBinding.inflate(inflater, container, false)
        val view = binding.root

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.cryptoRecyclerView.layoutManager = linearLayoutManager
        cryptoAdapter = CryptoRecyclerAdapter(cryptoList,this)
        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()

    }

//    Coroutine
    private fun loadData(){
        val contentType = "application/json".toMediaType()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(CryptoAPI::class.java)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch (exceptionHandler) {
            val response = retrofit.getData()

            supervisorScope {

                withContext(Dispatchers.Main + exceptionHandler) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            handleResponse(it);
                        }
                    }
                }

            }
        }
    }

//    RxJava
//    private fun loadData () {
//        val contentType = "application/json".toMediaType()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(Json.asConverterFactory(contentType))
//            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
//            .build()
//            .create(CryptoAPI::class.java)
//
//        compositeDisposable.add(retrofit
//            .getData()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(this::handleResponse))
//    }

    private fun handleResponse(cryptoResponse: CryptoResponse) {

        try {
            cryptoList.clear()
            cryptoList.addAll(cryptoResponse.data as ArrayList<Crypto> ?: emptyList())
            cryptoAdapter = CryptoRecyclerAdapter(cryptoList,this@CryptoListFragment)
            binding.cryptoRecyclerView.adapter = cryptoAdapter
        } catch (e: Exception) {
            println("JSON deserialization error: ${e.message}")
        }
    }

    override fun onItemClick(crypto: Crypto) {
        Toast.makeText(requireContext(),"Clicked : ${crypto.name}",Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
        job?.cancel()
    }
}

// Basic Retrofit call
//private fun loadData() {
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
//        call.enqueue(object : Callback<CryptoResponse> {
//            override fun onResponse(
//                call: Call<CryptoResponse>,
//                response: Response<CryptoResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val cryptoResponse = response.body()
//                    try {
//                        cryptoList.clear()
//                        cryptoList.addAll(cryptoResponse?.data as ArrayList<Crypto> ?: emptyList())
//                        cryptoAdapter = CryptoRecyclerAdapter(cryptoList,this@CryptoListFragment)
//                        binding.cryptoRecyclerView.adapter = cryptoAdapter
//
//                    } catch (e: Exception) {
//                        println("JSON deserialization error: ${e.message}")
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<CryptoResponse>, t: Throwable) {
//                println(t)
//            }
//
//        })
//    }