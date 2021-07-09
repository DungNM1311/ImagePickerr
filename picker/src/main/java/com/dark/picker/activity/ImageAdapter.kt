package com.dark.picker.activity

import android.view.LayoutInflater
import android.view.View
import com.dark.picker.R
import com.dark.picker.loadmore.adapter.BaseRclvVH
import com.dark.picker.loadmore.adapter.LoadMoreAdapter
import com.dark.picker.model.MediaGallery
import kotlinx.android.synthetic.main.item_image.view.*

class ImageAdapter : LoadMoreAdapter() {
    override fun getLoadingLayoutRes(): Int {
        return R.layout.item_load_more
    }

    override fun getLayoutResByViewType(viewType: Int): Int {
        return R.layout.item_image
    }

    override fun onCreateVHInfo(itemView: View, viewType: Int): BaseRclvVH<*> {
        return ImageVH(itemView)
    }

    inner class ImageVH(itemView: View) : BaseRclvVH<MediaGallery>(itemView) {
        override fun onBind(data: MediaGallery) {
            itemView.ivImageItem.setImageBitmap()
        }

        override fun onBind(data: MediaGallery, payload: List<Any>) {

        }

    }
}