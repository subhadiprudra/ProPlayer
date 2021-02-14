package com.easylife.proplayer;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.easylife.proplayer.App.CHANNEL_ID;

public class BasicFunctions {

    Context context;

    public BasicFunctions(Context context) {
        this.context = context;
    }

    public void write( String key, String data){
        SharedPreferences sharedPreferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(key,data);
        editor.commit();

    }

    public String read(String key){
        SharedPreferences sharedPreferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"nf");
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class IsNetworkAvailable implements Callable<Boolean>{

        @Override
        public Boolean call() throws Exception {
            try {
                Jsoup.connect("https://google.com").timeout(6000).get();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    public boolean isInternetOn(){
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<Boolean> on = executorService.submit(new IsNetworkAvailable());
        try {
            return on.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    public String getDateTime(){
        String  currentDateTimeString = DateFormat.getDateTimeInstance()
                .format(new Date());

        return currentDateTimeString;
    }


    public void showNotification(String title,String message) {

        Intent intent = new Intent(context,DashBoard.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(context,0,intent,0);


        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        long[] vibrate = { 0, 1000, 2000, 3000 };


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentText(message)
                .setContentTitle(title)
                .setContentIntent(pendingIntent);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.noti_icon2);
            notification.setColor(context.getResources().getColor(R.color.background));
        } else {
            notification.setSmallIcon(R.drawable.noti_icon2);
        }

        Notification notification1 = notification.build();

        notification1.defaults |= Notification.DEFAULT_VIBRATE;



        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(m, notification1);
    }








}
