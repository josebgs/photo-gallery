package api

import com.bignerdranch.android.photogallery.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {

    lateinit var galleryItems: List<GalleryItem>
}