package com.sanjoo.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.sanjoo.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.airbnb.lottie.LottieAnimationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding


    //07273196e2ae4640c76e37f7b5c9c906

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        fetchWeatherData(cityName = "")
        searchCity()
    }
    private fun searchCity(){
        val searchViewBar=binding.serachView
        searchViewBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { fetchWeatherData(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        /*searchViewBar.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })*/
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityName,"07273196e2ae4640c76e37f7b5c9c906","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temprature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windSpeed=responseBody.wind.speed
                    val sunrise=responseBody.sys.sunrise.toLong()
                    val sunset=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknownn"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min

                    binding.temprature.text=temprature
                    binding.tvWeather.text=condition
                    binding.tvMax.text="MaxTemp: $maxTemp c"
                    binding.tvMin.text="MinTemp: $minTemp c"
                    binding.humidity.text="$humidity %"
                    binding.windSpeed.text="$windSpeed m/s"
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset )}"
                    binding.sea.text="$seaLevel hPa"
                    binding.tvWeather.text=condition
                    binding.tvDay.text=dayName(System.currentTimeMillis())
                    binding.tvDate.text=date()
                    binding.cityName.text="$cityName"
                    binding.sunny.text=condition


//                    Log.d("TAG","onResponse: $temprature")
//                    Log.d("TAG","onResponse: $humidity")
//                    Log.d("TAG","onResponse: $minTemp")
//                    Log.d("TAG","onResponse: $seaLevel")

                    changeImageAccordingToConditions(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeImageAccordingToConditions(conditions:String) {
        when(conditions){
            "Clouds","Partly Cloud","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnnimation.setAnimation(R.raw.cloud)
            }
             "Clear Sky","Clear","Sunny"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnnimation.setAnimation(R.raw.sun)
            }
             "Light Rain","Drizzle","Showers","Heavy Rain","Moderate Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnnimation.setAnimation(R.raw.rain)
            }
             "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnnimation.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnnimation.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnnimation.playAnimation()
    }

    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    fun time(timestamp:Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
    private fun date( ):String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}