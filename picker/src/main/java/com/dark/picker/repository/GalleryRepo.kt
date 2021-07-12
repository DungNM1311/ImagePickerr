package com.dark.picker.repository

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.dark.picker.model.AlbumGallery
import com.dark.picker.model.MediaGallery

object GalleryRepo {
    fun getListGalleryPhoto(
        context: Context,
        albumId: String?,
        page: Int,
        limit: Int
    ): MutableList<MediaGallery> {
        val resolver = context.contentResolver
        val listPhoto: MutableList<MediaGallery> = mutableListOf()
        val externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.IS_PRIVATE,
            MediaStore.Images.ImageColumns.DATE_MODIFIED
        )
        val selection =
            if (albumId.isNullOrEmpty()) null else "${MediaStore.Images.ImageColumns.BUCKET_ID} = ?"
        val selectionArgs = if (albumId.isNullOrEmpty()) null else arrayOf(albumId)
        val orderBy = MediaStore.Images.ImageColumns.DATE_MODIFIED //order data by modified
        val cursorPhoto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver?.query(
                externalUri,
                projection,
                Bundle().apply {
                    // Selection
                    putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                    putStringArray(
                        ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                        selectionArgs
                    )
                    // Sort function
                    putStringArray(
                        ContentResolver.QUERY_ARG_SORT_COLUMNS,
                        arrayOf(MediaStore.Images.ImageColumns.DATE_MODIFIED)
                    )
                    putInt(
                        ContentResolver.QUERY_ARG_SORT_DIRECTION,
                        ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                    )
                    // Limit & Offset
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, page * limit)
                }, null
            )
        } else {
            resolver?.query(
                externalUri,
                projection,
                selection,
                selectionArgs,
                "$orderBy DESC LIMIT $limit OFFSET ${page * limit}"
            )//get all data in Cursor by sorting in DESC order
        }

        cursorPhoto?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val path =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                    val id =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                    listPhoto.add(MediaGallery().apply {
                        this.id = id
                        this.path = path
                    })
                } while (cursor.moveToNext())
            }
        }

        return listPhoto
    }

    fun getListGalleryAlbum(context: Context): MutableList<AlbumGallery> {
        val resolver = context.contentResolver
        val albums = mutableListOf<AlbumGallery>()
        val externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projectionsAlbum = arrayOf(
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
        )
        val albumCursor = resolver?.query(externalUri, projectionsAlbum, null, null, null)
        albumCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val bucketIdIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID)
                val nameIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)

                do {
                    val bucketId = cursor.getString(bucketIdIndex)
                    val bucketName = cursor.getString(nameIndex)

                    val selection =
                        if (bucketId.isNullOrEmpty()) null else "${MediaStore.Images.ImageColumns.BUCKET_ID} = ?"
                    val selectionArgs = if (bucketId.isNullOrEmpty()) null else arrayOf(bucketId)

                    val album = AlbumGallery().apply {
                        id = bucketId
                        name = bucketName
                    }
                    val isContains = albums.any {
                        it.id == bucketId
                    }

                    if (isContains) {
                        continue
                    }
                    albums.add(album)

                    val projections = arrayOf(
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA
                    )

                    val sortOrder = "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"
                    resolver.query(externalUri, projections, selection, selectionArgs, sortOrder)
                        ?.use { imageCursor ->
                            if (imageCursor.moveToFirst()) {
                                val imageUriIndex =
                                    imageCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
                                album.imagePath = imageCursor.getString(imageUriIndex)
                                album.count = imageCursor.count.toLong()
                            }
//                         imageCursor.close()
                        }

                } while (cursor.moveToNext())
            }
//             cursor.close()
        }
        return albums
    }
}