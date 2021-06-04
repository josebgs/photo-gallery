package com.bignerdranch.android.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import api.FlickrApi
import paging.PhotoGalleryPagingSource

private const val PAGE_SIZE = 100
class PhotoGalleryViewModel(): ViewModel() {

    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = PAGE_SIZE)
    ) {
        PhotoGalleryPagingSource(FlickrApi.getApiService())
    }.flow
        .cachedIn(viewModelScope)



}