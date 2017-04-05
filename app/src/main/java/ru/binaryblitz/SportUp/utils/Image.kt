package ru.binaryblitz.SportUp.utils

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

import ru.binaryblitz.SportUp.custom.AvatarView
import ru.binaryblitz.SportUp.models.User

object Image {
    fun loadAvatar(context: Context, user: User?, path: String?, imageView: ImageView) {
        if ((path == null || path.isEmpty() || path == "null") && user != null) {
            setAvatar(context, user, imageView)
            return
        }

        val callback = object : Callback {
            override fun onSuccess() {
            }

            override fun onError() {
                setAvatar(context, user!!, imageView)
            }
        }

        Picasso.with(context)
                .load(path)
                .fit()
                .centerCrop()
                .into(imageView, callback)
    }

    private fun setAvatar(context: Context, user: User,
                          imageView: ImageView) {
        imageView.setImageDrawable(AvatarView(context, user))
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
