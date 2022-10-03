package com.sunnyweather.android.ui.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication

class PlaceFragment: Fragment() {
    //获取viewModel实例
    private val viewModel by lazy{ ViewModelProviders.of(this).get(PlaceViewModel::class.java)}
    private lateinit var adapter: PlaceAdapter
    private lateinit var recycleView: RecyclerView
    private lateinit var inflate: View
    private lateinit var editText: EditText
    private lateinit var imageView: ImageView

    //Fragment创建视图时调用
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //实例化布局对象
        inflate = inflater.inflate(R.layout.fragment_place, container, false)
        recycleView = inflate.findViewById(R.id.recyclerView)
        imageView = inflate.findViewById(R.id.bgImageView)
        editText = inflate.findViewById(R.id.searchPlaceEdit)
        return inflate
    }

    //确保与Fragment相关联的Activity已经创建完毕时调用
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //创建线性布局
        val layoutManager = LinearLayoutManager(activity)
        //创建适配器
        adapter = PlaceAdapter(this,viewModel.placeList)
        //给recycleView设置适配器和主布局
        recycleView.layoutManager = layoutManager
        recycleView.adapter = adapter
        //当文本编辑框发生变化时会执行
        editText.addTextChangedListener{
            val content = it.toString()
            //如果编辑框内同不为空则搜索否则显示背景并清空数据
            if(content.isNotEmpty()){
                viewModel.searchPlaces(content)
            }else{
                recycleView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()

            }
        }
        //当服务器传递的返回结果发生改变时会被观察
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer {
            //获取结果或为null
            val places = it.getOrNull()
            //不为空则因此背景并显示列表刷新适配器的数据
            if(places!=null){
                recycleView.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                //情况集合里的数据并重写
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(activity,"未能检查到任何地点",Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
        })
    }

}