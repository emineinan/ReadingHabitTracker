package com.hms.readinghabittracker.utils

import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object PermissionUtils {
    const val ACCESS_FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
    const val ACCESS_COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
    const val LOCATION_REQUEST_CODE = 100
    const val PERMISSION_REQUEST_CODE_CAMERA = 1
    const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1045
    const val REQUEST_PICK_IMAGE = 1001

    fun hasLocationPermissions(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
}