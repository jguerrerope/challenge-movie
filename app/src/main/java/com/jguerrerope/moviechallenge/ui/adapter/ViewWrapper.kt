package com.jguerrerope.moviechallenge.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * This class wraps a view inside RecyclerView.ViewHolder
 *
 * @param V The subtype of View
 * @param view the view to be used by the holder
 */
class ViewWrapper<out V: View>(val view: V) : RecyclerView.ViewHolder(view)