package com.jguerrerope.moviechallenge.ui.adapter

import android.arch.lifecycle.Observer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ProgressBar
import com.jguerrerope.moviechallenge.data.Movie
import com.jguerrerope.moviechallenge.data.NetworkState
import com.jguerrerope.moviechallenge.ui.item.MovieItemView

/**
 * PagedListAdapter used to display Movie
 *
 * @param onItemClick what has to be done when a MovieItemView gets clicked
 */
class MoviePagedListAdapter(
        private val onItemClick: (movie: Movie) -> Unit
) : PagedListAdapterBase<Movie>(diffCallback) {
    private var hasExtraRow = false

    var itemParentWithPercentage = -1f
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutManager.canScrollVertically()
    }

    // We create a regular MovieItemView or a ProgressBar that indicates that we are loading more repos
    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            VIEW_TYPE_PROGRESS -> ProgressBar(parent.context)
            VIEW_TYPE_MOVIE -> MovieItemView(parent.context)
            else -> throw RuntimeException("bad type view")
        }.apply {
            layoutParams = if (itemParentWithPercentage in 0.0..1.0) {
                val newWidth = (parent.measuredWidth * itemParentWithPercentage).toInt()
                LayoutParams(newWidth, WRAP_CONTENT)
            }else  LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    }

    // We bind data to our view. In case of null items we can set the view as placeholder for the data that will arrive
    // at some point
    override fun onBindViewHolder(holder: ViewWrapper<View>, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_MOVIE) {
            (holder.view as MovieItemView).apply {
                val item = getItem(position)
                if (item == null) {
                    setOnClickListener(null)
                } else {
                    setOnClickListener { onItemClick(item) }
                    bind(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow && position == itemCount - 1) VIEW_TYPE_PROGRESS else VIEW_TYPE_MOVIE
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow) 1 else 0

    // We observe changes in network state. So if it starts loading for more objects we set the extra row with the
    // progress bar. And when it finish we remove it from the bottom.
    val networkStateObserver = Observer<NetworkState> {
        it?.let { state ->
            hasExtraRow = when (state) {
                NetworkState.NEXT_LOADING -> {
                    notifyItemInserted(super.getItemCount()); true
                }
                else -> {
                    if (hasExtraRow) notifyItemRemoved(itemCount); false
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_PROGRESS = 1
        private const val VIEW_TYPE_MOVIE = 2

        private val diffCallback = object : DiffUtil.ItemCallback<Movie>() {

            // Lets assume that the name is a unique identifier for a repo. Even if
            // item content change at some point, the name will define if it is the
            // same item or it is another one
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                    oldItem.title == newItem.title

            // Repo is a data class so it has predefined equals method where each
            // field is used to define if two objects are equals
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                    oldItem == newItem
        }
    }
}