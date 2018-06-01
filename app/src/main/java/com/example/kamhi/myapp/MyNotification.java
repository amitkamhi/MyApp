package com.example.kamhi.myapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kamhi on 18/10/2017.
 */

public class MyNotification {
//sent notifications to users
    final int PENDING_REQUEST = 0;
    Notification.Builder builder;
    static NotificationManager notiManager = null;
    public final static int NOTIF1 = 1;
    Context context;

    MyNotification(Context context, Class classNotification){
        this.context = context;
        Intent myIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_REQUEST, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new Notification.Builder(context);
        if(classNotification == MyService.class) {
            builder.setTicker("My Service")
                    .setContentTitle("PicaPic want to wish you")
                    .setSmallIcon(R.drawable.birthday_cake)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(false)
                    .setVibrate(new long[]{100, 100, 100, 100, 100})
                    .setSound(Uri.parse("uri://sadfasdfasdf.mp3"));
        }
        else{
            builder.setTicker("Photo Details")
                    .setContentTitle("deal")
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(Uri.parse("uri://sadfasdfasdf.mp3"));
        }
        if(notiManager == null){
            notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    public void update(int notificationId, String message){
        builder.setContentText(message).setOnlyAlertOnce(false);
//        if (bitmap != null){
//           builder.setStyle(new Notification.BigPictureStyle().bigPicture(bitmap));
//        }
        Notification noti = builder.build();
        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notiManager.notify(notificationId, noti);
    }


    public void stop(int notificationId){
        notiManager.cancel(notificationId);
    }
}
