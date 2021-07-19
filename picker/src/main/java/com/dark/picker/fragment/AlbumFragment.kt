package com.dark.picker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dark.picker.CustomCallable
import com.dark.picker.R
import com.dark.picker.fragment.adapter.AlbumAdapter
import com.dark.picker.model.AlbumGallery
import com.dark.picker.repository.GalleryRepo
import com.dark.picker.utils.TaskRunner
import com.dark.picker.utils.safeLog
import kotlinx.android.synthetic.main.fragment_albums.*

class AlbumFragment : Fragment() {

    var onItemSelected: (AlbumGallery) -> Unit = {}
    var taskRunner: TaskRunner? = null

    private val albumAdapter: AlbumAdapter by lazy {
        AlbumAdapter()
    }
    private var listAlbumGallery: List<AlbumGallery>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_albums,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAction()
        loadData()
    }

    private fun initView() {
        rclvAlbum.adapter = albumAdapter
    }

    private fun initAction() {
        albumAdapter.onItemClick = onItemSelected
    }

    private fun loadData() {
        if (listAlbumGallery != null) {
            albumAdapter.reset(listAlbumGallery)
            return
        }
        taskRunner?.executeAsync(object : CustomCallable<List<AlbumGallery>?> {
            override fun setDataAfterLoading(result: List<AlbumGallery>?) {
                listAlbumGallery = result
                albumAdapter.reset(listAlbumGallery)
            }

            override fun setUiForLoading() {

            }

            override fun call(): List<AlbumGallery>? {
                var list: List<AlbumGallery>? = null
                try {
                    list = GalleryRepo.getListGalleryAlbum(context!!, true)
                } catch (e: Exception) {
                    e.safeLog()
                }
                return list
            }

        })
    }
}