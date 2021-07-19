package com.dark.picker.model

class ImageGallery() {
    var id: String? = null
    var path: String? = null
    var orderSelected: Int = -1
    var sourceType: SourceType = SourceType.LIBRARY
    var mediaType: MediaType = MediaType.IMAGE
    var size: Long? = null
}