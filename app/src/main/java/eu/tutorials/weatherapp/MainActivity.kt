package eu.tutorials.weatherapp

import android.app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.SearchView

import eu.tutorials.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//appid=fd9b347e6e7b4ae848b1720106ecbb88
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Bangalore")
        searchcity()
    }
    fun searchcity(){
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
               if(query!=null){
                   fetchWeatherData(query)
               }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
            return true
            }

        })
     }

    private fun fetchWeatherData(cityname : String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response= retrofit.getWeatherData(cityname, "fd9b347e6e7b4ae848b1720106ecbb88", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature= responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxtemp = responseBody.main.temp_max
                    val mintemp = responseBody.main.temp_min

                    binding.temp.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxtemp.text="Max Temp : $maxtemp °C"
                    binding.mintemp.text="Min Temp : $mintemp °C"
                    binding.humidity.text="$humidity%"
                    binding.windspeed.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$sealevel"
                    binding.condition.text=condition
                    binding.day.text=dayname(System.currentTimeMillis())
                    binding.date.text= date()
                    binding.cityname.text="$cityname"

                    changeimagesaccweather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })

    }
    fun changeimagesaccweather(conditions: String){
        when (conditions){
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)

        }

        "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
            binding.root.setBackgroundResource(R.drawable.colud_background)
            binding.lottieAnimationView2.setAnimation(R.raw.cloud)

        }

        "Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Thunderstorm" -> {
            binding.root.setBackgroundResource(R.drawable.rain_background)
            binding.lottieAnimationView2.setAnimation(R.raw.rain)

        }

        else->{
            binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnimationView2.setAnimation(R.raw.sun)

        }

    }
    binding.lottieAnimationView2.playAnimation()
    }
    fun time(timestamp: Long):String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayname(timestamp: Long):String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}