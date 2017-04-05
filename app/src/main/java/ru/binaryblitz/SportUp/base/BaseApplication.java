package ru.binaryblitz.SportUp.base;

import android.app.Application;
import android.support.multidex.MultiDex;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
