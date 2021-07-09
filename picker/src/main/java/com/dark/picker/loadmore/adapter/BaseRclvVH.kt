package com.dark.picker.loadmore.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dark.picker.utils.setSafeOnClickListener

abstract class BaseRclvVH<T>(itemView: View) : ViewHolder(itemView), IOnBind<T> {

    init {
        onInitView(itemView)
    }

    final override fun onInitView(itemView: View) {

    }

    abstract override fun onBind(data: T)

    abstract override fun onBind(data: T, payload: List<Any>)

    fun clickOn(
        view: View,
        listener: View.OnClickListener?
    ) {
        if (listener != null) {
            view.setSafeOnClickListener {
                if (bindingAdapterPosition > -1) {
                    listener.onClick(view)
                }
            }
        }
    }

    fun longClickOn(
        view: View,
        listener: View.OnLongClickListener?
    ) {
        if (listener != null) {
            view.setOnLongClickListener {
                if (bindingAdapterPosition > -1) {
                    return@setOnLongClickListener listener.onLongClick(view)
                }
                true
            }
        }
    }
}