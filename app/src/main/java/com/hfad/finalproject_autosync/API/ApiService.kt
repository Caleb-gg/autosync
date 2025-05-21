package com.hfad.finalproject_autosync.API

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/vehicles/DecodeVin/{vin}?format=json")
    fun getAPIData(@Path("vin") vin: String) : Call<DataAPIResponse>
}