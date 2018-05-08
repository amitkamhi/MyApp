package com.example.kamhi.myapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Kamhi on 21/1/2018.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // set offline capabilities
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if(!FirebaseApp.getApps(MyApp.this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        /*
        Picasso.Builder builder = new Picasso.Builder(MyApp.this);
        builder.downloader(new OkHttpDownloader(MyApp.this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    */
    }
}
