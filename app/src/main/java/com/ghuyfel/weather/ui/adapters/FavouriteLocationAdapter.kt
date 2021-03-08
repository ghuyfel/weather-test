package com.ghuyfel.weather.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ghuyfel.weather.R
import com.ghuyfel.weather.databinding.ListItemFavouriteLocationBinding
import com.ghuyfel.weather.repository.models.FavouriteLocation
import com.ghuyfel.weather.ui.interfaces.FavouriteLocationClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class FavouriteLocationAdapter @Inject constructor() :
        RecyclerView.Adapter<FavouriteLocationAdapter.FavouriteLocationViewHolder>() {

    private var favouriteLocations = ArrayList<FavouriteLocation>()
    private lateinit var listener: FavouriteLocationClickListener

    fun setListener(l: FavouriteLocationClickListener) {
        listener = l
    }

    fun submitList(locations: List<FavouriteLocation>) {
        favouriteLocations.clear()
        favouriteLocations.addAll(locations)
        favouriteLocations.sortBy { it.locationName }
        notifyDataSetChanged()
    }

    fun insertItem(location: FavouriteLocation) {
        favouriteLocations.add(location)
        favouriteLocations.sortBy { it.locationName }
        val position = favouriteLocations.indexOf(location)
        notifyItemInserted(position)
    }

    fun delete(location: FavouriteLocation) {
        val position = favouriteLocations.indexOf(location)
        if (position > -1) {
            favouriteLocations.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteLocationViewHolder {
        val binding = ListItemFavouriteLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavouriteLocationViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: FavouriteLocationViewHolder, position: Int) {
        holder.bind(favouriteLocations[position])
    }

    override fun getItemCount(): Int = favouriteLocations.size

    class FavouriteLocationViewHolder(
        private val binding: ListItemFavouriteLocationBinding,
        private val listener: FavouriteLocationClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var favouriteLocation: FavouriteLocation

        fun bind(location: FavouriteLocation) {
            val context = binding.root.context
            favouriteLocation = location
            binding.tv.text = location.locationName
            binding.iv.setImageDrawable(
                if (location.isCurrentLocation)
                    ContextCompat.getDrawable(context, R.drawable.ic_my_location)
                else
                    ContextCompat.getDrawable(context, R.drawable.ic_star)
            )

            binding.root.setOnClickListener {
                listener.onFavouriteLocationClicked(favouriteLocation)
            }

            binding.root.setOnLongClickListener {
                MaterialAlertDialogBuilder(binding.root.context)
                    .setTitle(R.string.warning)
                    .setMessage(
                        binding.root.context.getString(
                            R.string.confirm_deletion,
                            favouriteLocation.locationName
                        )
                    )
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        run {
                            listener.onDeleteButtonClicked(favouriteLocation)
                            dialog.cancel()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        run {
                            dialog.cancel()
                        }
                    }.show()
                true
            }
        }
    }
}