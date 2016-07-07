package me.vijitdhingra.tasklist;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * Created by Vijit on 08-05-2016.
 */
public class NotificationPublisher extends BroadcastReceiver{

    //constants
    public final static String INTENT_EXTRA_NOTIFICATION="NOTIFICATION";
    public final static String INTENT_EXTRA_NOTIFICATION_ID="NOTIFICATION_ID";

    public void onReceive(Context context, Intent intent){
        Notification notification = intent.getParcelableExtra(INTENT_EXTRA_NOTIFICATION);
        int notificationId = intent.getIntExtra(INTENT_EXTRA_NOTIFICATION_ID,-1);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId,notification);
    }

}
