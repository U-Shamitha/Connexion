package com.example.srp_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Context;

public class NotificationHelper extends AppCompatActivity {


    //Firebase notification
    public static void displayNotification(Context context, String title, String body) {

        //Toast.makeText(context,"here it is working",Toast.LENGTH_LONG).show();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.search)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat mNotificationMngr = NotificationManagerCompat.from(context);

        mNotificationMngr.notify(1, mBuilder.build());


    }
}
