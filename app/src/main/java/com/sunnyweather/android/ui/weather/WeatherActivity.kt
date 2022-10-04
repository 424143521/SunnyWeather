package com.sunnyweather.android.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Sky
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.WeatherViewModel
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }
    lateinit var placeName: TextView
    lateinit var currentTemp: TextView
    lateinit var currentSky: TextView
    lateinit var currentAQI: TextView
    lateinit var nowLayout: RelativeLayout
    lateinit var forecastLayout: LinearLayout
    lateinit var coldRiskText: TextView
    lateinit var dressingText: TextView
    lateinit var ultravioletText: TextView
    lateinit var carWashingText: TextView
    lateinit var weatherLayout: ScrollView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //融合状态栏
        //拿到装饰视图
        val decorView = window.decorView
        //表示Activity布局会显示状态栏上面
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //把状态栏设置为透明色
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_weather)

        placeName = findViewById(R.id.placeName)
        currentTemp = findViewById(R.id.currentTemp)
        currentSky = findViewById(R.id.currentSky)
        currentAQI = findViewById(R.id.currentAQI)
        nowLayout = findViewById(R.id.nowLayout)
        forecastLayout = findViewById(R.id.forecastLayout)
        coldRiskText = findViewById(R.id.coldRiskText)
        dressingText = findViewById(R.id.dressingText)
        ultravioletText = findViewById(R.id.ultravioletText)
        carWashingText = findViewById(R.id.carWashingText)
        weatherLayout = findViewById(R.id.weaterLayout)
        //如果第一次为空则在intent中读取
        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        //给weatherLiveData设置观察者，当数据改变则刷新ui
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if(weather != null){
                //不为空刷新ui
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        //改变locationLiveData的值并改变weatherLiveData的值
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
    }

    //进行解析与展示
    private fun showWeatherInfo(weather : Weather) {
        placeName.text = viewModel.placeName
        var realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()}°C"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //预报
        //填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for(i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]

            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()}°C"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        //生活指数
        //填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE

    }


}