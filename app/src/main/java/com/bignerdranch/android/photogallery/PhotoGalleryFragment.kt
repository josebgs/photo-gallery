package com.bignerdranch.android.photogallery

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.floor


private const val TAG = "PhotoGalleryFragment"
private const val COLUMN_SIZE = 120 //dp

class PhotoGalleryFragment: Fragment(){

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private var photoAdapter = PhotoAdapter(GalleryItemComparator)

    companion object{
        fun newInstance() = PhotoGalleryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoGalleryViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)

        //default spanCount changes when the global layout state or the visibility of views
        // within the view tree changes

        val viewTreeObserver = photoRecyclerView.viewTreeObserver

        viewTreeObserver.addOnGlobalLayoutListener {
            val spanCount =
                floor(photoRecyclerView.width / COLUMN_SIZE.toPx()).toInt()
            (photoRecyclerView.layoutManager as GridLayoutManager).spanCount = spanCount
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoRecyclerView.adapter = photoAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            photoGalleryViewModel.flow.collectLatest { pagingData ->
                photoAdapter.submitData(pagingData)
            }
        }
    }

    private inner class PhotoHolder(itemTextView: TextView)
        : RecyclerView.ViewHolder(itemTextView){

            val bindTitle: (CharSequence?) -> Unit = itemTextView::setText
        }

    private inner class PhotoAdapter(diffCallback: DiffUtil.ItemCallback<GalleryItem>)
        :PagingDataAdapter<GalleryItem, PhotoHolder>(diffCallback){

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PhotoHolder {
            val textView = TextView(parent.context)
            return PhotoHolder(textView)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = getItem(position)
            //Log.d(TAG, "${galleryItem?.title}")
            holder.bindTitle(galleryItem?.title ?: "")
        }
    }

    private object GalleryItemComparator : DiffUtil.ItemCallback<GalleryItem>() {
        override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
            return oldItem == newItem
        }
    }
    
    private fun Int.toPx(): Double = (this * Resources.getSystem().displayMetrics.density).toDouble()
}