package ru.binaryblitz.sportup.utils

import android.content.Context
import android.widget.ImageView

import com.squareup.picasso.Picasso

object Image {
    fun loadPhoto(context: Context, path: String?, imageView: ImageView) {
        if (path == null || path == "null") {
            return
        }

        Picasso.with(context)
                .load(path)
                .fit()
                .centerInside()
                .into(imageView)
    }
}
