package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//访问彩云天气城市搜索API的Retrofit接口
interface PlaceService {
    //去服务器发送一条get请求，并将返回值映射为一个对象
    @GET("v2/place?token =${SunnyWeatherApplication.TOKEN}&LANG=zh_CN")
    fun searchPlaces(@Query("query") query:String): Call<PlaceResponse>
}