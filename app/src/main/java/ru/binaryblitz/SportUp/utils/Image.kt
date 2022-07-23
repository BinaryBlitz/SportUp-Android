package ru.binaryblitz.SportUp.utils

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ru.binaryblitz.SportUp.custom.AvatarView

object Image {
    fun loadAvatar(context: Context, name: String?, path: String?, imageView: ImageView) {
        if ((path == null || path.isEmpty() || path == "null") && name != null) {
            setAvatar(context, name, imageView)
            return
        }

        val callback = object : Callback {
            override fun onSuccess() {
            }

            override fun onError() {
                setAvatar(context, name!!, imageView)
            }
        }

        Picasso.with(context)
                .load(path)
                .fit()
                .centerCrop()
                .into(imageView, callback)
    }

    private fun setAvatar(context: Context, name: String,
                          imageView: ImageView) {
        imageView.setImageDrawable(AvatarView(context, name))
    }

    fun loadPhoto(path: String?, imageView: ImageView) {
        if (path == null || path.isEmpty() || path == "null") {
            return
        }

        Picasso.with(imageView.context)
                .load(path)
                .fit()
                .centerCrop()
                .into(imageView)
    }

    fun loadResourse(path: Int, imageView: ImageView) {
        if (path <= 0) {
            return
        }

        Picasso.with(imageView.context)
                .load(path)
                .fit()
                .centerCrop()
                .into(imageView)
    }
}
