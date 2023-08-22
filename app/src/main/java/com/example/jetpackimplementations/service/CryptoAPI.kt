package com.example.jetpackimplementations.service

import com.example.jetpackimplementations.model.Crypto
import com.example.jetpackimplementations.model.CryptoResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface CryptoAPI {

    @Headers("X-CMC_PRO_API_KEY:faf55cd0-9e90-4926-8756-5d18a40803c5")
    @GET("cryptocurrency/listings/latest?start=1&limit=5000&convert=USD")
    suspend fun getData() : Response<CryptoResponse> //Coroutine

//    RxJava
//    fun getData() : Observable<CryptoResponse>


//    Retrofit call
//    fun getData(): Call<CryptoResponse>

}