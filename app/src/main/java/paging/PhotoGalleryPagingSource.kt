package paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import api.FlickrApi
import com.bignerdranch.android.photogallery.FlickrFetchr
import com.bignerdranch.android.photogallery.GalleryItem
import retrofit2.HttpException
import java.io.IOException


/**
 * PhotoGallery page-keyed PagingSource, which uses Int page number to load pages.
 *
 * Loads Items from network requests via Retrofit to a backend service.
 *
 * Note that the key type is Int, since we're using page number to load a page.
 */

class PhotoGalleryPagingSource(private val backend: FlickrApi): PagingSource<Int, GalleryItem>() {
    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.

        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        // Retrofit calls that return the body type throw either IOException for network
        // failures, or HttpException for any non-2xx HTTP status codes. This code reports all
        // errors to the UI, but you can inspect/wrap the exceptions to provide more context.
        return try {
            // Key may be null during a refresh, if no explicit key is passed into Pager
            // construction. Use 1 as default, because our API is indexed started at index 1
            val pageNumber = params.key ?: 1

            // Suspending network load via Retrofit. This doesn't need to be wrapped in a
            // withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine
            // CallAdapter dispatches on a worker thread.
            val response = backend.fetchPhotos(pageNumber)
            val responseData = mutableListOf<GalleryItem>()
            val data = response.body()?.galleryItems?.filterNot {
                it.url.isBlank()
            } ?: emptyList()

            responseData.addAll(data)

            // Since 1 is the lowest page number, return null to signify no more pages should
            // be loaded before it.
            val prevKey = if (pageNumber > 1) pageNumber - 1 else null

            // This API defines that it's out of data when we are on the 4th page (only 5 pages max according to docs)
            val nextKey = if (pageNumber <= 4) pageNumber + 1 else null
            LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }

    }
}