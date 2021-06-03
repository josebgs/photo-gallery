package api

import android.util.Log
import com.bignerdranch.android.photogallery.GalleryItem
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class PhotoDeserializer: JsonDeserializer<PhotoResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse {

        val response = json?.asJsonObject
        val photoBody = response?.get("photos")?.asJsonObject
        val photos = photoBody?.get("photo")?.asJsonArray
        val galleryItems = mutableListOf<GalleryItem>()

        photos?.forEach {
            val photo = it.asJsonObject
            val title = photo.get("title").asString
            val id = photo.get("id").asString
            val url = photo.get("url_s").asString
            galleryItems.add(GalleryItem(title, id, url))
        }

        val photoResponse = PhotoResponse()
        photoResponse.galleryItems = galleryItems
        return photoResponse
    }
}