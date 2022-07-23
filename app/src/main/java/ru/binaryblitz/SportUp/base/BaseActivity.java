package ru.binaryblitz.SportUp.base;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import ru.binaryblitz.SportUp.R;
import ru.binaryblitz.SportUp.deps.DaggerDependencies;
import ru.binaryblitz.SportUp.deps.Dependencies;
import ru.binaryblitz.SportUp.server.ServerApi;

public class BaseActivity extends AppCompatActivity {
    protected Dependencies dependencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File cacheFile = new File(getCacheDir(), "responses");
        dependencies = DaggerDependencies.builder().serverApi(new ServerApi(cacheFile)).build();
    }

    protected Dependencies dependencies() {
        return dependencies;
    }

    public void onInternetConnectionError() {
        Snackbar.make(findViewById(R.id.main), R.string.lost_connection, Snackbar.LENGTH_SHORT).show();
    }

    public void  onLocationError() {
        Snackbar.make(findViewById(R.id.main), R.string.location_error, Snackbar.LENGTH_SHORT).show();
    }
}
