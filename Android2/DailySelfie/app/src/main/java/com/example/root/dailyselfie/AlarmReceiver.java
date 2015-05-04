package com.example.root.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.R;

/**
 * Created by root on 4/05/15.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final int MY_NOTIFICATION_ID=1;
    Intent in;
    PendingIntent pendingIntent;
    Notification mBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
        // For our recurring task, we'll just display a message
        in=new Intent(context,MainActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent=PendingIntent.getActivity(context, 0, in, 0);

        mBuilder=new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_menu_camera)
                .setContentTitle("Selfie time!")
                .setContentText("take a selfie with this awesome dailyselfie app")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder);
        mNotificationManager.notify(MY_NOTIFICATION_ID, mBuilder);
    }
}
