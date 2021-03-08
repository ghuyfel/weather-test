package com.ghuyfel.weather.ui.customs

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import com.ghuyfel.weather.ui.factories.FragmentFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CustomNavHostFragment: NavHostFragment() {
    @ExperimentalCoroutinesApi
    @Inject
    lateinit var fragmentFactory: FragmentFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = fragmentFactory
    }
}