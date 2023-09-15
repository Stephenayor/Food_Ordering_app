package com.example.yummy.utils

import android.content.Context
import android.widget.Toast

class Tools {

    companion object {
        fun showToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun showShortToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}