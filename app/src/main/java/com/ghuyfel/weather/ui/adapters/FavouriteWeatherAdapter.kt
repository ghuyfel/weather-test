package com.ghuyfel.weather.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ghuyfel.weather.databinding.ListItemFavouriteInfoForecastBinding
import com.ghuyfel.weather.ui.models.ForecastWeather
import com.ghuyfel.weather.utils.ResourceUtils
import com.ghuyfel.weather.utils.StringUtils
import javax.inject.Inject

class FavouriteWeatherAdapter @Inject constructor(): RecyclerView.Adapter<FavouriteWeatherAdapter.FavouriteWeatherViewHolder>() {

    private val forecasts = ArrayList<ForecastWeather>()

    fun submitList(values: List<ForecastWeather>) {
        forecasts.apply {
            clear()
            addAll(values)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteWeatherViewHolder =
        FavouriteWeatherViewHolder(
            ListItemFavouriteInfoForecastBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )


    override fun onBindViewHolder(holder: FavouriteWeatherViewHolder, position: Int) = holder.bind(forecast = forecasts[position])

    override fun getItemCount(): Int = forecasts.size


    class FavouriteWeatherViewHolder(private val binding: ListItemFavouriteInfoForecastBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(forecast: ForecastWeather) {
            binding.tvTmp.text = StringUtils.formatTemperature(forecast.temp)
            binding.tvDay.text = forecast.dayOfTheWeek
            binding.iv.setImageDrawable(
                ContextCompat.getDrawable(binding.root.context,
                ResourceUtils.getIconForWeatherCondition(forecast.weather)))
        }
    }
}