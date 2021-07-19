package com.dark.picker.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dark.picker.R
import com.dark.picker.model.AlbumGallery
import com.dark.picker.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.item_album.view.*

class AlbumAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: (AlbumGallery) -> Unit = {}

    private val mDataSet = mutableListOf<AlbumGallery>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlbumVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_album,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlbumVH) {
            holder.onBind(mDataSet[position])
        }
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    fun reset(list: List<AlbumGallery>?) {
        mDataSet.clear()
        if (list != null) {
            mDataSet.addAll(list)
        }
        notifyDataSetChanged()
    }

    private inner class AlbumVH(view: View) : RecyclerView.ViewHolder(view) {


        init {
            itemView.setSafeOnClickListener {
                onItemClick(mDataSet[bindingAdapterPosition])
            }
        }


        fun onBind(data: AlbumGallery) {
            itemView.tvAlbumItemName.text = data.name
            itemView.tvAlbumItemTotalItem.text = data.count.toString()
            Glide.with(itemView.ivAlbumItem)
                .load(data.imagePath)
                .centerCrop()
                .placeholder(R.drawable.shape_blue)
                .into(itemView.ivAlbumItem)
        }
    }
}