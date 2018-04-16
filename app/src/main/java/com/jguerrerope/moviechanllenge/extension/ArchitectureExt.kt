package com.jguerrerope.moviechanllenge.extension

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations

/**
 * ViewModel
 */
fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?) -> Unit) =
        observe(owner, Observer<T> { v -> observer.invoke(v) })

fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> =
        Transformations.switchMap(this, func)
