package com.dark.picker.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.dark.picker.CustomCallable
import com.dark.picker.R
import com.dark.picker.loadmore.LoadMoreRecyclerView
import com.dark.picker.model.AlbumGallery
import com.dark.picker.model.MediaGallery
import com.dark.picker.repository.GalleryRepo
import com.dark.picker.utils.safeLog
import kotlinx.android.synthetic.main.activity_image_picker.*

class ImagePickerActivity : Activity() {

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
        rclvImagePickerImage.setLoadDataListener(object : LoadMoreRecyclerView.IOnLoadMoreRecyclerViewListener {
            override fun onLoadData() {
                loadData()
            }

            override fun onLoadEmptyData(isEmpty: Boolean) {

            }
        })
        rclvImagePickerImage.layoutManager = GridLayoutManager(this, 3)
        loadData()
    }

    private fun loadData() {
        if (isLoading) {
            return
        }
        isLoading = true
        taskRunner.executeAsync<List<MediaGallery>>(object : CustomCallable<List<MediaGallery>?> {
            override fun setDataAfterLoading(result: List<MediaGallery>?) {
                if (pageIndex == 0) {
                    adapter.reset(result)
                    stillMore = true
                    adapter.showLoadMore(stillMore)
                    pageIndex++
                } else {
                    if (result.isNullOrEmpty()) {
                        stillMore = false
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
                    list = GalleryRepo.getListGalleryPhoto(this@ImagePickerActivity, null, pageIndex, limit)
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