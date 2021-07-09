package com.dark.picker.loadmore.adapter

import android.view.View

interface IOnBind<T> {
    fun onInitView(itemView: View)
    fun onBind(data: T)
    fun onBind(data: T, payload: List<Any>)
}