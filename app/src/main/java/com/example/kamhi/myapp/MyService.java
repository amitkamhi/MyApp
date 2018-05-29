package com.example.kamhi.myapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kamhi on 3/10/2017.
 */

public class MyService extends Service {
//The service, sent happy birthday to user
    Worker myWorker;
    MyNotification notif;
    SimpleDateFormat fmt;
    Date userBirthday = new Date();
    Date todaysDate;

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        askForUserBirthday();
        myWorker = new Worker();
        myWorker.start();
        showNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    /*public String getHello() {
        return "Hello Service";
    }*/

    public void askForUserBirthday(){
        FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.currentUserUid).child("birthday").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fmt = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    String birthday = dataSnapshot.getValue().toString();
                    userBirthday = fmt.parse(birthday);
                    synchronized (userBirthday){
                        userBirthday.notifyAll();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showNotification(){
        this.notif = new MyNotification(this, MyService.class);
    }

    public void hideNotification(){
        if(this.notif!=null){
            this.notif.stop(MyNotification.NOTIF1);
            this.notif = null;
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            super.run();
            synchronized (userBirthday) {
                while (userBirthday==null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    checkDate();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    return;
                }
            }
        }
    }

        private void checkDate(){
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("dd.MM.yyyy");
            String strDate = mdformat.format(calendar.getTime());
            try {
                todaysDate = fmt.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(todaysDate);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTime(userBirthday);
            int userMonth = calendar.get(Calendar.MONTH);
            int userDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (userMonth==month && userDay==day) {
                    Log.i("today", "user birthday is today");
                if (notif!=null){
                    notif.update(MyNotification.NOTIF1, "Happy birthday", null);
                }
            }
            Log.i("birthday",userBirthday.toString());
        }

        public class MyBinder extends Binder {
            public MyService getService() {
                return MyService.this;
            }
        }
    }

