package com.dark.picker.loadmore.adapter

import android.view.View
import androidx.annotation.LayoutRes

abstract class LoadMoreAdapter : BaseRclvAdapter(), ILoadmoreAdapter {

    private var isShowLoadMore = false

    companion object {
        const val LOADING_TYPE = -1
    }

    var onAddItemListener: IOnAddItemListener? = null

    override fun getItemViewType(position: Int): Int {
        val data = mDataSet[position]
        if (data is LoadingVHData) {
            return LOADING_TYPE
        }
        return super.getItemViewType(position)
    }

    override fun getLayoutResource(viewType: Int): Int {
        return when {
            viewType == LOADING_TYPE -> getLoadingLayoutRes()
            viewType < LOADING_TYPE -> throw RuntimeException("viewType need to more than -1")
            else -> getLayoutResByViewType(viewType)
        }
    }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> {
        return when (viewType) {
            LOADING_TYPE -> LoadingVH(itemView)
            else -> onCreateVHInfo(itemView, viewType)
        }
    }

    override fun showLoadMore(isShown: Boolean) {
        if (isShown) {
            if (!isShowLoadMore) {
                isShowLoadMore = true
                addItemAndNotify(LoadingVHData(null))
            }
        } else {
            if (isShowLoadMore) {
                isShowLoadMore = false
                removeItemAndNotify(mDataSet.size - 1)
            }
        }
    }

    override fun isLoadMoreSupport(): Boolean = isShowLoadMore

    override fun enableLoadMore(isLoadMore: Boolean) {
        isShowLoadMore = isLoadMore
    }

    override fun reset(newItems: List<*>?) {
        onAddItemListener?.onAdded(0, newItems?.size ?: 0)
        mDataSet.clear()
        addItems(newItems)
        if (isShowLoadMore) {
            addItem(LoadingVHData(null))
        }
        notifyDataSetChanged()
    }

    open fun addMoreItem(itemList: List<*>, stillMore: Boolean) {
        val startIndex = if (isShowLoadMore) mDataSet.size - 1 else mDataSet.size
        if (itemList.isNullOrEmpty()) {
            onAddItemListener?.onAdded(startIndex, 0)
        } else {
            addItemsAtIndex(itemList, startIndex)
            if (stillMore) {
                if (!isShowLoadMore) {
                    isShowLoadMore = true
                    addItem(LoadingVHData(null))
                    notifyItemRangeInserted(startIndex, itemList.size + 1)
                } else {
                    notifyItemRangeInserted(startIndex, itemList.size)
                }
            } else {
                if (isShowLoadMore) {
                    isShowLoadMore = false
                    notifyItemRangeInserted(startIndex, itemList.size)
                    removeItemAndNotify(mDataSet.size - 1)
                } else {
                    notifyItemRangeInserted(startIndex, itemList.size)
                }
            }
            onAddItemListener?.onAdded(startIndex, itemList.size)
        }
    }

    @LayoutRes
    abstract fun getLoadingLayoutRes(): Int

    @LayoutRes
    abstract fun getLayoutResByViewType(viewType: Int): Int

    abstract fun onCreateVHInfo(itemView: View, viewType: Int): BaseRclvVH<*>


    class LoadingVHData(data: Any?) : BaseVHData<Any?>(data) {
        init {
            type = LOADING_TYPE
        }
    }

    inner class LoadingVH(itemView: View) : BaseRclvVH<LoadingVHData>(itemView) {
        override fun onBind(data: LoadingVHData) {

        }

        override fun onBind(data: LoadingVHData, payload: List<Any>) {

        }
    }

    interface IOnAddItemListener {
        fun onAdded(startIndex: Int, count: Int)
    }
}