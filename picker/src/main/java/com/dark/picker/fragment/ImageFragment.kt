package com.dark.picker.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dark.picker.CustomCallable
import com.dark.picker.R
import com.dark.picker.fragment.adapter.ImageAdapter
import com.dark.picker.loadmore.LoadMoreRecyclerView
import com.dark.picker.model.AlbumGallery
import com.dark.picker.model.ImageGallery
import com.dark.picker.repository.GalleryRepo
import com.dark.picker.utils.TaskRunner
import com.dark.picker.utils.safeLog
import kotlinx.android.synthetic.main.fragment_photos.*

class ImageFragment : Fragment() {

    private val adapter: ImageAdapter by lazy {
        ImageAdapter()
    }

    var onSelectItem: (Boolean) -> Unit = {

    }

    var taskRunner: TaskRunner? = null

    private var albumGallery: AlbumGallery? = null
    private var pageIndex: Int = 0
    private val limit = 20
    private var stillMore: Boolean = false
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_photos,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAction()
        Log.e("NONAME", "onViewCreated")
        loadData()
    }

    fun setAlbum(album: AlbumGallery?) {
        albumGallery = album
        resetData()
    }

    fun resetData(){
        pageIndex = 0
        isLoading = false
        stillMore = true
        adapter.showLoadMore(stillMore)
        adapter.reset(mutableListOf<ImageGallery>())
    }

    fun getListUriImageSelected(): List<Uri> {
        return adapter.getListUriImageSelected()
    }

    private fun initView() {
        rclvImagePickerImage.adapter = adapter
        rclvImagePickerImage.layoutManager = GridLayoutManager(context, 3)
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

        adapter.onSelectItem = onSelectItem
    }

    private fun loadData() {
        if (isLoading) {
            return
        }
        isLoading = true
        taskRunner?.executeAsync(object : CustomCallable<List<ImageGallery>?> {
            override fun setDataAfterLoading(result: List<ImageGallery>?) {
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

            override fun call(): List<ImageGallery>? {
                var list: List<ImageGallery>? = null
                try {
                    list = GalleryRepo.getListGalleryPhoto(
                        context!!,
                        albumGallery?.id,
                        pageIndex,
                        limit
                    )
                } catch (e: Exception) {
                    e.safeLog()
                } finally {
                    isLoading = false
                }

                return list
            }

        })
    }

    interface CallBack

}