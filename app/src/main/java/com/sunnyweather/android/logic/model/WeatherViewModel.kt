package com.sunnyweather.android.logic.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository

class WeatherViewModel : ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()
    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    //将返回的LiveData对象转换一个可供Activity观察的LiveData对象
    val weatherLiveData = Transformations.switchMap(locationLiveData){
        Repository.refreshWeather(it.lng,it.lat)
    }

    //会把Location封装成一个对象赋值给locationLiveData对象
    fun refreshWeather(lng: String, lat: String){
        locationLiveData.value = Location(lng,lat)
    }
}