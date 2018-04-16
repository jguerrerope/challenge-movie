package com.jguerrerope.moviechallenge.ui.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.View
import android.view.ViewGroup

/**
 * This class allows to use a View as a the ViewHolder of a PagedListAdapter
 *
 * @param diffCallback it is used to check item state and update views if some item has changed
 */
abstract class PagedListAdapterBase<T>(diffCallback: DiffUtil.ItemCallback<T>) :
        PagedListAdapter<T, ViewWrapper<View>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewWrapper<View> =
            ViewWrapper(onCreateItemView(parent, viewType))

    /**
     * Create the view that will be used
     */
    abstract fun onCreateItemView(parent: ViewGroup, viewType: Int): View
}