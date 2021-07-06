package com.dark.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.dark.picker.activity.ImagePickerActivity

object ImagePickerBuilder {
    fun start(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001
            )
            return;
        } else {
            activity.startActivityForResult(
                Intent(activity, ImagePickerActivity::class.java),
                1000
            )
        }
    }
}