package com.ghuyfel.weather.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ghuyfel.weather.databinding.ListItemFavouriteInfoForecastBinding
import com.ghuyfel.weather.databinding.ListItemForecastBinding
import com.ghuyfel.weather.ui.models.ForecastWeather
import com.ghuyfel.weather.utils.ResourceUtils
import com.ghuyfel.weather.utils.StringUtils
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class ForecastAdapter @Inject constructor(): RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private val forecasts = ArrayList<ForecastWeather>()

    fun submitList(values: List<ForecastWeather>) {
        forecasts.apply {
            clear()
            addAll(values)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder =
        ForecastViewHolder(ListItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) = holder.bind(forecast = forecasts[position])

    override fun getItemCount(): Int = forecasts.size

    class ForecastViewHolder(private val homeBinding: ListItemForecastBinding): RecyclerView.ViewHolder(
        homeBinding.root
    ) {

        fun bind(forecast: ForecastWeather) {
            homeBinding.tvTemp.text = StringUtils.formatTemperature(forecast.temp)
            homeBinding.tvDay.text = forecast.dayOfTheWeek
            homeBinding.ivForecast.setImageDrawable(ContextCompat.getDrawable(homeBinding.root.context,
                ResourceUtils.getIconForWeatherCondition(forecast.weather)))
        }

    }
}