package ru.binaryblitz.sportup.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Image {
    public static void loadPhoto(final Context context, final String path, final ImageView imageView) {
        if (path == null || path.equals("null")) {
            return;
        }

        Picasso.with(context)
                .load(path)
                .fit()
                .centerInside()
                .into(imageView);
    }
}
