package ru.binaryblitz.SportUp.utils;

import android.content.Context;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ru.binaryblitz.SportUp.custom.AvatarView;
import ru.binaryblitz.SportUp.models.User;

public class Image {
    public static void loadAvatar(final Context context, final User user, final String path, final ImageView imageView) {
        if (path == null || path.isEmpty() || path.equals("null")) {
            setAvatar(context, user, imageView);
            return;
        }

        Callback callback = new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                setAvatar(context, user, imageView);
            }
        };

        Picasso.with(context)
                .load(path)
                .fit()
                .centerCrop()
                .into(imageView, callback);
    }

    private static void setAvatar(final Context context, final User user,
                                  final ImageView imageView) {
        imageView.setImageDrawable(new AvatarView(context, user));
    }

    public static void loadPhoto(final String path, final ImageView imageView) {
        if (path == null || path.isEmpty() || path.equals("null")) {
            return;
        }

        Picasso.with(imageView.getContext())
                .load(path)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    public static void loadResourse(final int path, final ImageView imageView) {
        if (path <= 0) {
            return;
        }

        Picasso.with(imageView.getContext())
                .load(path)
                .fit()
                .centerCrop()
                .into(imageView);
    }
}
