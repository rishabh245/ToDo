package com.example.rishabh.todo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by rishabh on 8/9/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       // Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();
        generateNotifiaction(context , intent);

    }

    private void generateNotifiaction(Context context, Intent intent) {
        NotificationCompat.Builder notififactionBuilder = new  NotificationCompat.Builder(context);
        Bundle b = intent.getExtras();
        String title = b.getString("title");
        String time = b.getString("time");
        int id = b.getInt("id");
        notififactionBuilder.setContentTitle(title)
                .setContentText("Task at" + time)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("New Task");
        NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        nm.notify(id , notififactionBuilder.build());
    }
}
