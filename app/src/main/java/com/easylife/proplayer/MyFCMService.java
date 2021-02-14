package com.easylife.proplayer;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;


public class MyFCMService extends FirebaseMessagingService {


    BasicFunctions basicFunctions;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        basicFunctions= new BasicFunctions(getApplicationContext());

        basicFunctions.write("token",s);
        if(!basicFunctions.read("id").equals("nf")) {
            FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("token").setValue(s);
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null){
            Log.d("xxxxxxxxxxxxxxxxxx",remoteMessage.getNotification().getBody());
            basicFunctions= new BasicFunctions(getApplicationContext());
            basicFunctions.showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

        }

    }
}
