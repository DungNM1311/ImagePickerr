package com.dark.picker.activity

import android.Manifest
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.dark.picker.R
import com.dark.picker.fragment.AlbumFragment
import com.dark.picker.fragment.ImageFragment
import com.dark.picker.utils.TaskRunner
import com.dark.picker.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_image_picker.*


class ImagePickerActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_REQUEST = 1888
        private const val MY_CAMERA_PERMISSION_CODE = 100
    }

    private var imageFragment: ImageFragment? = null
    private var albumFragment: AlbumFragment? = null
    private var taskRunner: TaskRunner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)
        taskRunner = TaskRunner()
        initView()
        initAction()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageFragment = null
        albumFragment = null
        taskRunner = null
    }

    private fun initView() {
        initImageFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.flImagePickerContainer, imageFragment!!, "IMAGE_FRAGMENT")
            .commit()
    }

    private fun initAction() {
        ivImagePickerClose.setSafeOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        llImagePickerChooseAlbum.setSafeOnClickListener {
            if (albumFragment == null) {
                initAlbumFragment()
            }
            ivImagePickerTakePhoto.isVisible = true
            tvImagePickerDone.isVisible = false
            supportFragmentManager.beginTransaction()
                .replace(R.id.flImagePickerContainer, albumFragment!!, "ALBUM_FRAGMENT")
                .commit()
        }

        tvImagePickerDone.setSafeOnClickListener {
            val intent = Intent()
            var clip: ClipData? = null
            val listUri = imageFragment?.getListUriImageSelected() ?: mutableListOf()
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

    private fun initImageFragment() {
        imageFragment = ImageFragment()
        imageFragment?.taskRunner = taskRunner
        imageFragment?.onSelectItem = {
            ivImagePickerTakePhoto.isVisible = !it
            tvImagePickerDone.isVisible = it
        }
    }

    private fun initAlbumFragment() {
        albumFragment = AlbumFragment()
        albumFragment?.taskRunner = taskRunner
        albumFragment?.onItemSelected = {
            ivImagePickerTakePhoto.isVisible = true
            tvImagePickerDone.isVisible = false
            tvAlbumName.text = it.name
            imageFragment?.setAlbum(it)
            supportFragmentManager.beginTransaction()
                .replace(R.id.flImagePickerContainer, imageFragment!!, "IMAGE_FRAGMENT")
                .commit()
        }
    }

}