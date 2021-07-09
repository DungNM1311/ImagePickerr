package com.dark.picker.loadmore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRclvAdapter : RecyclerView.Adapter<BaseRclvVH<Any?>>() {
    protected var mDataSet = mutableListOf<Any?>()

    abstract fun getLayoutResource(viewType: Int): Int
    abstract fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*>

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseRclvVH<Any?> {
        val layout = getLayoutResource(viewType)
        val v = LayoutInflater.from(viewGroup.context).inflate(layout, viewGroup, false)
        return onCreateVH(v, viewType) as BaseRclvVH<Any?>
    }

    override fun onBindViewHolder(
        baseRclvHolder: BaseRclvVH<Any?>,
        position: Int
    ) {
        baseRclvHolder.onBind(getItemDataAtPosition(position))
    }

    override fun onBindViewHolder(baseRclvHolder: BaseRclvVH<Any?>, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(baseRclvHolder, position, payloads)
        } else {
            baseRclvHolder.onBind(getItemDataAtPosition(position), payloads)
        }
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    fun getItemDataAtPosition(position: Int): Any {
        return mDataSet[position]!!
    }

    fun addItem(item: Any?) {
        mDataSet.add(item)
    }

    fun addItems(items: List<Any?>?) {
        if (items.isNullOrEmpty()) {
            return
        }
        mDataSet.addAll(items)
    }

    fun addItemAndNotify(item: Any?) {
        mDataSet.add(item)
        notifyItemInserted(mDataSet.size - 1)
    }

    fun addItemAtIndexAndNotify(index: Int, item: Any?) {
        mDataSet.add(index, item)
        notifyItemInserted(index)
    }

    fun addItemsAtIndex(items: List<Any?>?, index: Int) {
        if (items.isNullOrEmpty()) {
            return
        }
        mDataSet.addAll(index, items)
    }

    fun addItemAtIndex(item: Any?, index: Int) {
        mDataSet.add(index, item)
    }

    fun addItemsAndNotify(items: List<*>) {
        val start = mDataSet.size
        mDataSet.addAll(items)
        notifyItemRangeInserted(start, items.size)
    }

    fun removeItemAndNotify(index: Int) {
        mDataSet.removeAt(index)
        notifyItemRemoved(index)
    }

    open fun reset(newItems: List<*>?) {
        mDataSet.clear()
        addItems(newItems)
        notifyDataSetChanged()
    }

    fun remove(index: Int) {
        mDataSet.removeAt(index)
        notifyItemRemoved(index)
    }

    fun clearData() {
        mDataSet.clear()
    }
}