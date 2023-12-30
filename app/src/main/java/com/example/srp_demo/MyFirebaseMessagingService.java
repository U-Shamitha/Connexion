package com.example.srp_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        //Toast.makeText(this, "message recieved", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, remoteMessage.toString(), Toast.LENGTH_SHORT).show();

        if(remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            //NotificationHelper.displayNotification(getApplicationContext(),title,body);
        }

        //playing audio when user receives notification
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),notification);
        r.play();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            r.setLooping(false);
        }

        //vibration
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern,-1);

        int resourceImage = getResources().getIdentifier(remoteMessage.getNotification().getIcon(),"drawable",getPackageName());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,MainActivity.CHANNEL_ID);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setSmallIcon(R.drawable.icon);
        } else{
            builder.setSmallIcon(R.drawable.icon);
        }

        Intent resultIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(resourceImage);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId ="Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);

        }

        mNotificationManager.notify(100,builder.build());

    }





}