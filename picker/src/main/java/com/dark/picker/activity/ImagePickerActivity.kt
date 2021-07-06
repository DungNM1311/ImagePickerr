package com.dark.picker.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.dark.picker.R
import com.dark.picker.repository.GalleryRepo

class ImagePickerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)

        val list = GalleryRepo.getListGalleryAlbum(this)
        Log.e("17864234980", "onCreate: ${list.size}")
    }
}