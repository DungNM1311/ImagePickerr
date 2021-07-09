package com.dark.picker.loadmore.adapter

interface ILoadmoreAdapter {
    fun showLoadMore(isShown: Boolean)
    fun isLoadMoreSupport(): Boolean
    fun enableLoadMore(isLoadMore: Boolean)
}
