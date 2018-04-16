package com.jguerrerope.moviechallenge.ui.viewmodel

import android.arch.lifecycle.LiveData

class AbsentLiveData<T> private constructor(firstDefault: T? = null) : LiveData<T>() {
    init {
        postValue(firstDefault)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }

        fun <T> create(value: T): LiveData<T> {
            return AbsentLiveData(value)
        }
    }
}