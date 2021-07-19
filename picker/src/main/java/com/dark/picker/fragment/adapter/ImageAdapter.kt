package com.dark.picker.fragment.adapter

import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.dark.picker.R
import com.dark.picker.loadmore.adapter.BaseRclvVH
import com.dark.picker.loadmore.adapter.LoadMoreAdapter
import com.dark.picker.model.ImageGallery
import com.dark.picker.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.item_image.view.*

class ImageAdapter : LoadMoreAdapter() {

    companion object {
        const val PAYLOAD_SELECTED = 1
        const val MAX_ITEM_SELECTED = 30
    }

    var onSelectItem: (Boolean) -> Unit = {

    }

    private var maxSizeSelected: Int = MAX_ITEM_SELECTED

    private val listSelected = mutableListOf<MediaGalleryData>()

    override fun getLoadingLayoutRes(): Int {
        return R.layout.item_load_more
    }

    override fun getLayoutResByViewType(viewType: Int): Int {
        return R.layout.item_image
    }

    override fun onCreateVHInfo(itemView: View, viewType: Int): BaseRclvVH<*> {
        return ImageVH(itemView)
    }

    override fun reset(newItems: List<*>?) {
        listSelected.clear()
        val list = newItems?.map {
            MediaGalleryData(it as ImageGallery)
        }
        super.reset(list)
    }

    override fun addMoreItem(itemList: List<*>, stillMore: Boolean) {
        val list = itemList?.map {
            MediaGalleryData(it as ImageGallery)
        }
        super.addMoreItem(list, stillMore)
    }

    fun setMaxSizeSelected(max: Int) {
        maxSizeSelected = max
        if (listSelected.size > maxSizeSelected) {
            listSelected.subList(0, maxSizeSelected - 1)
        }
        notifyDataSetChanged()
    }

    fun getListUriImageSelected(): List<Uri> {
        return listSelected.map {
            Uri.parse(it.data.path)
        }
    }

    private fun getIndexSelected(item: MediaGalleryData): Int {
        return listSelected.indexOfFirst {
            it.data.id == item.data.id
        } + 1
    }

    private fun findIndexOfItem(item: MediaGalleryData): Int {
        return mDataSet.indexOfFirst {
            item.data.id == (it as? MediaGalleryData)?.data?.id
        }
    }

    inner class ImageVH(itemView: View) : BaseRclvVH<MediaGalleryData>(itemView) {

        init {
            itemView.setSafeOnClickListener {
                val item = mDataSet[bindingAdapterPosition] as? MediaGalleryData
                    ?: return@setSafeOnClickListener
                if (item.isSelected) {
                    val indexRemove = listSelected.indexOfFirst {
                        item.data.id == it.data.id
                    }
                    if (indexRemove <= -1) {
                        return@setSafeOnClickListener
                    }
                    item.isSelected = false
                    val listSub = listSelected.slice(indexRemove until listSelected.size)
                    listSelected.removeAt(indexRemove)
                    notifyItemChanged(bindingAdapterPosition, PAYLOAD_SELECTED)
                    listSub.forEach {
                        notifyItemChanged(findIndexOfItem(it), PAYLOAD_SELECTED)
                    }
                } else {
                    item.isSelected = true
                    listSelected.add(item)
                    notifyItemChanged(bindingAdapterPosition, PAYLOAD_SELECTED)
                }
                onSelectItem(listSelected.isNotEmpty())
            }
        }

        override fun onBind(data: MediaGalleryData) {
            selectedItem(data)
            Glide.with(itemView.ivImageItem)
                .load(data.data.path)
                .centerCrop()
                .placeholder(R.drawable.shape_blue)
                .into(itemView.ivImageItem)
        }

        override fun onBind(data: MediaGalleryData, payload: List<Any>) {
            payload.forEach {
                if (it is Int && it == PAYLOAD_SELECTED) {
                    selectedItem(data)
                }
            }
        }

        private fun selectedItem(data: MediaGalleryData) {
            itemView.vSelected.isSelected = data.isSelected
            itemView.tvNumber.isSelected = data.isSelected
            if (data.isSelected) {
                itemView.tvNumber.text = getIndexSelected(data).toString()
            } else {
                itemView.tvNumber.text = null
            }
        }

    }


    inner class MediaGalleryData(val data: ImageGallery) {
        var isSelected = false
    }
}