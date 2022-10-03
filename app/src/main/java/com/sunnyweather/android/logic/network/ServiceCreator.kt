package com.sunnyweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

//Retrofit的构建器
object ServiceCreator {
    //请求的根Url
    private const val BASE_URL = "https://api.caiyunapp.com/"
    //实例化retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())//设计Call的解析器把jesson转换为对象
        .build()
    //获取serviceClass接口的动态代理对象
    fun<T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    //以泛型的方式获取serviceClass接口的动态代理对象
    inline fun<reified T> create():T = create(T:: class.java)
}