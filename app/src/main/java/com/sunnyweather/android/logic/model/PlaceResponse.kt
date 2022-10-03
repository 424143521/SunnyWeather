package com.sunnyweather.android.logic.model

import android.location.Location
import com.google.gson.annotations.SerializedName

//附近地点
data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(val name: String, val location: Location,
            @SerializedName("formatted_address") val address: String)

//位置
data class Location(val lng: String, val lat: String)