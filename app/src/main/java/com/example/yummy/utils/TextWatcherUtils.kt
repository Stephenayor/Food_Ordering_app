package com.example.yummy.utils

import android.text.Editable
import android.text.TextWatcher

object TextWatcherUtils {
    fun createTextWatcher(
        beforeTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null,
        onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null,
        afterTextChanged: ((Editable?) -> Unit)? = null
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeTextChanged?.invoke(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged?.invoke(s, start, before, count)
            }

            override fun afterTextChanged(s: Editable?) {
                afterTextChanged?.invoke(s)
            }
        }
    }
}
