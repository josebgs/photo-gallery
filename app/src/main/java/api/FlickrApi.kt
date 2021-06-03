package api

import com.bignerdranch.android.photogallery.BuildConfig
import retrofit2.Call
import retrofit2.http.GET

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=${BuildConfig.FLICKR_KEY}" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s"
    )
    fun fetchPhotos(): Call<FlickrResponse>


}