package com.sunnyweather.android.logic

import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.RealtimeResponse
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext


//负责链接UI层面的ViewModel
object Repository {
    //searchLiveData发生变化时会执行此方法并返回数据给界面
    //根据代码块中的代码会运行在子线程IO中
    fun searchPlaces(query: String) = fire(Dispatchers.IO){

            //搜索城市数据
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            //如果服务器相应的状态是ok
            if (placeResponse.status == "ok") {
                //places为一个存放附近地点的集合
                val places = placeResponse.places
                //把值赋给result
                Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }
        //把结果传为PlaceViewModel中的placeLiveData

//刷新天气，
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {

    //创建协程作用域，子线程或作用域中的代码没执行完会阻塞当前协程
    coroutineScope {
        //通过async创建个子协程获取今天的天气
        val deferredRealtime = async {
            SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
        }
        //通过async创建个子协程获取未来天气
        val deferredDaily = async {
            SunnyWeatherNetwork.getDailyWeather(lng, lat)
        }
        //当子线程执行完会执行这两个await方法如果请求状态ok则包裹结果否则包裹异常
        val realtimeResponse = deferredRealtime.await()
        val dailyResponse = deferredDaily.await()
        if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
            val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
            Result.success(weather)
        } else {
            Result.failure(
                RuntimeException(
                    "realtime response status is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}"
                )
            )
        }
    }
}
//入参一个上下文，一个函数参数，但是要在函数参数前添加suspend关键字，让传入的lambada表达时拥有挂起函数上下文
    private fun<T> fire(context: CoroutineContext,block: suspend () -> Result<T>)=
        liveData<Result<T>>(context) {
            val result = try{
                block()
            } catch (e: Exception){
                Result.failure<T>(e)
            }
            //提交数据
            emit(result)
        }
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavePlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}


