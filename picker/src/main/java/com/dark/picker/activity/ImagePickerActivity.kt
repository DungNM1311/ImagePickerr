package com.dark.picker.activity

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.dark.picker.CustomCallable
import com.dark.picker.R
import com.dark.picker.loadmore.LoadMoreRecyclerView
import com.dark.picker.model.MediaGallery
import com.dark.picker.repository.GalleryRepo
import com.dark.picker.utils.safeLog
import com.dark.picker.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_image_picker.*


class ImagePickerActivity : Activity() {

    companion object {
        private const val CAMERA_REQUEST = 1888
        private const val MY_CAMERA_PERMISSION_CODE = 100    }

    private val adapter: ImageAdapter by lazy {
        ImageAdapter()
    }

    private var pageIndex: Int = 0
    private val limit = 20
    private var stillMore: Boolean = false
    private val taskRunner: TaskRunner by lazy {
        TaskRunner()
    }
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)
        rclvImagePickerImage.adapter = adapter
        initAction()
        rclvImagePickerImage.layoutManager = GridLayoutManager(this, 3)
        loadData()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun initAction() {
        rclvImagePickerImage.setLoadDataListener(object :
            LoadMoreRecyclerView.IOnLoadMoreRecyclerViewListener {
            override fun onLoadData() {
                loadData()
            }

            override fun onLoadEmptyData(isEmpty: Boolean) {

            }
        })

        adapter.onSelectItem = {
            Log.e("NONAME", "onSelectItem $it")
            ivImagePickerTakePhoto.isVisible = !it
            tvImagePickerDone.isVisible = it
        }
        tvImagePickerDone.setSafeOnClickListener {
            val intent = Intent()
            var clip: ClipData? = null
            val listUri = adapter.getListUriImageSelected()
            for (uri in listUri) {
                if (clip == null) {
                    clip = ClipData(null, arrayOf(), ClipData.Item(uri))
                } else {
                    clip.addItem(ClipData.Item(uri))
                }
            }
            intent.clipData = clip
            setResult(RESULT_OK, intent)
            finish()
        }
        ivImagePickerTakePhoto.setSafeOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    MY_CAMERA_PERMISSION_CODE
                )
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        }
    }

    private fun loadData() {
        if (isLoading) {
            return
        }
        isLoading = true
        taskRunner.executeAsync(object : CustomCallable<List<MediaGallery>?> {
            override fun setDataAfterLoading(result: List<MediaGallery>?) {
                if (pageIndex == 0) {
                    adapter.reset(result)
                    stillMore = true
                    adapter.showLoadMore(stillMore)
                    pageIndex++
                } else {
                    if (result.isNullOrEmpty()) {
                        stillMore = false
                        adapter.showLoadMore(false)
                    } else {
                        stillMore = true
                        adapter.addMoreItem(result, stillMore)
                        pageIndex++
                    }
                }
            }

            override fun setUiForLoading() {

            }

            override fun call(): List<MediaGallery>? {
                var list: List<MediaGallery>? = null
                try {
                    list = GalleryRepo.getListGalleryPhoto(
                        this@ImagePickerActivity,
                        null,
                        pageIndex,
                        limit
                    )
                    Log.e("NONAME", "$pageIndex --- ${list.size}")
                } catch (e: Exception) {
                    e.safeLog()
                } finally {
                    isLoading = false
                }

                return list
            }

        })
    }
}