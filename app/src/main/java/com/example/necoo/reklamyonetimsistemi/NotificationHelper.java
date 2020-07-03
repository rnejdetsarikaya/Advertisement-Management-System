package com.example.necoo.reklamyonetimsistemi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by NECOO on 23.04.2019.
 */

public class NotificationHelper extends ContextWrapper {

    Context base;

    private static final String CHANNEL_NAME = "Main Notifications";
    private  static final String CHANNEL_DESCRIPTION = "Notification description";
    private String CHANNEL_ID = "MyNotificationChannel";

    NotificationManager notificationManager;
    public NotificationHelper(Context base) {
        super(base);
        this.base = base;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(CHANNEL_DESCRIPTION);
        //notificationChannel.enableVibration(true);
       // notificationChannel.canShowBadge();
        //notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getNotificationManager().createNotificationChannel(notificationChannel);

    }

    public NotificationManager getNotificationManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager) base.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    public  void notify(String message, String title){
        Notification.Builder builder = new Notification.Builder(base,CHANNEL_ID);
        //Uri notficationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setContentTitle(title);
        builder.setContentText(message);
        //builder.setSound(notficationUri);
        builder.setStyle(new Notification.BigTextStyle().bigText(message));
        builder.setContentIntent(PendingIntent.getActivity(this,0,new Intent(base,ApplicationActivity.class),0));
        builder.setSmallIcon(R.drawable.ic_notification);
        getNotificationManager().notify(9001,builder.build());

    }
}
