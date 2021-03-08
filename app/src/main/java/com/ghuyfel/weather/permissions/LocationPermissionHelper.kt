package com.ghuyfel.weather.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object LocationPermissionHelper {
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun isPermissionGranted(context: Context) = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    fun shouldShowRationale(activity: Activity) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        false
    }

    fun requestLocationPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(permissions)
    }

}