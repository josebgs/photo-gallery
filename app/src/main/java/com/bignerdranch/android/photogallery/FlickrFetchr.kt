package com.bignerdranch.android.photogallery

import api.FlickrApi
import api.PhotoDeserializer
import api.PhotoResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "FlickrFetchr"

class FlickrFetchr { // Obsolete in this challenge. Keeping it for reference.

    private val flickrApi: FlickrApi

    init {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(PhotoResponse::class.java, PhotoDeserializer())
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    suspend fun fetchPhotos(page: Int): List<GalleryItem>{
        var items: List<GalleryItem> = mutableListOf()
        val flickrRequest: Response<PhotoResponse> = flickrApi.fetchPhotos(page)
/*
        flickrRequest.enqueue(object: Callback<PhotoResponse> {
            override fun onFailure(
                call: Call<PhotoResponse>, //original call object
                t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

            override fun onResponse(
                call: Call<PhotoResponse>, //original call object
                response: Response<PhotoResponse>
            ){
                Log.d(TAG, "Response received")
                val photoResponse: PhotoResponse? = response.body()
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems
                    ?: mutableListOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }
                items = galleryItems
            }
        })
*/
        return items
    }
}