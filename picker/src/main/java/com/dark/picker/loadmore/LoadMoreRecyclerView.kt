package com.dark.picker.loadmore

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dark.picker.loadmore.adapter.ILoadmoreAdapter
import com.dark.picker.loadmore.adapter.LoadMoreAdapter

class LoadMoreRecyclerView : RecyclerView {
    private var reverse = false
    private var mAdapter: Adapter<*>? = null
    private lateinit var mLayoutManager: LinearLayoutManager
    private var isLoading = false
    private var mLoadDataListener: IOnLoadMoreRecyclerViewListener? = null

    constructor(context: Context) : super(context) {
        initLayoutManager(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
        initLayoutManager(context)
        initScrollListener()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
        initLayoutManager(context)
        initScrollListener()
    }

    fun setLoadDataListener(listener: IOnLoadMoreRecyclerViewListener) {
        mLoadDataListener = listener
    }

    private fun init(attrs: AttributeSet?) {
//        if (attrs == null) {
//            return
//        }
//        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadMoreRecyclerView)
//        reverse = typedArray.getBoolean(R.styleable.LoadMoreRecyclerView_wrap_rclv_reverse, false)
//        typedArray.recycle()
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter is LoadMoreAdapter) {
            adapter.onAddItemListener = object : LoadMoreAdapter.IOnAddItemListener {
                override fun onAdded(startIndex: Int, count: Int) {
                    isLoading = false
                    if (count == 0) {
                        mLoadDataListener?.onLoadEmptyData(true)
                    } else {
                        post {
                            mLoadDataListener?.onLoadEmptyData(false)
                            // 1 for down and -1 for up
                            var direction = 1
                            if (reverse) {
                                direction = -1
                            }
                            if (!canScrollVertically(direction)
                                && adapter.isLoadMoreSupport()
                            ) {
                                isLoading = true
                                mLoadDataListener?.onLoadData()
                            }
                        }
                    }
                }
            }
            mAdapter = adapter
            super.setAdapter(mAdapter)
        } else {
            throw IllegalArgumentException("Adapter must be LoadmoreAdapter")
        }
    }


    override fun setLayoutManager(layout: LayoutManager?) {
        if (layout is LinearLayoutManager) {
            reverse = layout.reverseLayout
            super.setLayoutManager(layout)
            return
        }
        throw IllegalArgumentException("LayoutManager must be LinearLayoutManager")
    }

    private fun initLayoutManager(context: Context) {
        mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, reverse)
        layoutManager = mLayoutManager
    }

    private fun initScrollListener() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mAdapter !is LoadMoreAdapter ||
                    !(mAdapter as ILoadmoreAdapter).isLoadMoreSupport()
                ) {
                    return
                }

                if (mLoadDataListener == null) {
                    return
                }

                var checkRotation = dy > 0
                if (reverse) {
                    checkRotation = dy < 0
                }
                if (checkRotation && !isLoading) {
                    if(layoutManager is GridLayoutManager){
                        val totalItemCount = (layoutManager as GridLayoutManager).itemCount
                        val lastVisibleItem = (layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
                        if(totalItemCount <= (lastVisibleItem + 5)) {
                            isLoading = true
                            mLoadDataListener?.onLoadData()
                        }
                    } else {
                        val visibleItemCount: Int = mLayoutManager.childCount
                        val totalItemCount: Int = mLayoutManager.itemCount
                        val pastVisibleItems: Int = mLayoutManager.findFirstVisibleItemPosition()
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            isLoading = true
                            mLoadDataListener?.onLoadData()
                        }
                    }

                }
            }
        })
    }

    interface IOnLoadMoreRecyclerViewListener {
        fun onLoadData()
        fun onLoadEmptyData(isEmpty: Boolean)
    }
}