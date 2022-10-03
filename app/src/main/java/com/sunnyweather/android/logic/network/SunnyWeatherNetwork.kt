package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
//网络数据源访问入口
object SunnyWeatherNetwork {
    //创建一个访问附近地点接口的动态代理对象
    private val placeService = ServiceCreator.create(PlaceService::class.java)
    //发起搜索城市的数据请求，并将结果映射成一个对象
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()
    //await方法为Call的拓展函数并拥有上下文
    //这样当执行enqueue方法是会在子线程请求数据，完成后会切到主线程回调Callback
    private suspend fun <T> Call<T>.await():T{
        //将当前协程挂起，并再线程中执行Lambda中的代码，挂起是为了请求数据完成后好配合协程下面的代码
        return suspendCoroutine {
            enqueue(object : Callback<T>{
                //会自动判断当请求成功时
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    //如果数据不为空将则恢复被挂起的协程，并传入服务器相应的数据，此值会成为suspendCoroutine函数的返回值
                    if (body !=null) it.resume(body)
                    else it.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }
//                如果请求失败
                override fun onFailure(call: Call<T>, t: Throwable) {
                    it.resumeWithException(t)
                }

            })
        }
    }
}