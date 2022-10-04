package com.sunnyweather.android.logic.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel : ViewModel() {
    private val searchLiveData = MutableLiveData<String>()
    val placeList = ArrayList<Place>()
    //当数据发生变化时去服务器请求数据并把结果返回给Fragment
    //从服务器获取结果对象并返回一个可观察的结果对象,query时searchLiveData的value
    val placeLiveData = Transformations.switchMap(searchLiveData){query ->
        Repository.searchPlaces(query)
    }
    //当查询参数发生变化的话，switchMap会执行并从外部返回一个可观察的LiveData对象
    fun searchPlaces(query: String){
        searchLiveData.value = query
    }

    fun savePlace(place: Place) = Repository.savePlace(place)
    fun getSavedPlace() = Repository.getSavePlace()
    fun isPlaceSaved() = Repository.isPlaceSaved()
}