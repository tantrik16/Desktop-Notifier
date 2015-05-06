package com.tantrik.desktopnotifier;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by tantrik on 05/05/15.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i("Received data", "Received: " + extras.toString());
            }
        }
        final String SOME_ACTION = "com.tantrik.receive.GCM";
        Intent updateAdapter = new Intent(SOME_ACTION);
        updateAdapter.putExtra("message", extras.toString());
        updateAdapter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendBroadcast(updateAdapter);
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

}