package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.RuntimeException

//负责链接UI层面的ViewModel
object Repository {
    //searchLiveData发生变化时会执行此方法并返回数据给界面
    //根据代码块中的代码会运行在子线程IO中
    fun searchPlaces(query: String) = liveData(Dispatchers.IO){
        val result = try {
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
        }catch (e:Exception){
            Result.failure<List<Place>>(e)
        }
        //把结果传为PlaceViewModel中的placeLiveData
        emit(result)
        }
}