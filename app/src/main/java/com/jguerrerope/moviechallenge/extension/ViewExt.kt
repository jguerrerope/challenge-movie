package com.jguerrerope.moviechallenge.extension

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

/**
 * View
 */

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.visibleOrGone(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.visibleOrInvisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

/**
 * EditText
 */


fun EditText.textWatcher(
        afterTextChanged: (s: Editable?) -> Unit = {},
        beforeTextChanged: (s: CharSequence?) -> Unit = {},
        onTextChanged: (s: CharSequence?) -> Unit = {}
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s)
        }

    })
}

fun EditText.textWatcherOnArfterTextChanged(afterTextChanged: (s: Editable?) -> Unit = {}) {
    this.textWatcher(afterTextChanged = afterTextChanged)
}