package com.dark.imagepickerr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dark.picker.ImagePickerBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImagePickerBuilder.start(this)
    }
}