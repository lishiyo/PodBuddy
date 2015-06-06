//package com.cziyeli.podbuddy.services;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.IBinder;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.cziyeli.podbuddy.Config;
//import com.cziyeli.podbuddy.HomeActivity;
//import com.cziyeli.podbuddy.R;
//
///**
// * IntentService would be killed immediately
// */
//public class ForegroundService extends Service {
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent.getAction().equals(Config.ACTION.STARTFOREGROUND_ACTION)) {
//            Log.d(Config.DEBUG_TAG, "Received Start Foreground Intent ");
//
//            Intent notificationIntent = new Intent(this, HomeActivity.class);
//            notificationIntent.setAction(Config.ACTION.MAIN_ACTION);
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                    notificationIntent, 0);
//
//            /** Previous, Play, Next intents **/
//            // getService used for pendingIntents of notification actions
//            // getService => goes directly to onStartCommand() here instead of through an activity
//            Intent previousIntent = new Intent(this, ForegroundService.class);
//            previousIntent.setAction(Config.ACTION.PREV_ACTION);
//            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
//                    previousIntent, 0);
//
//            Intent playIntent = new Intent(this, ForegroundService.class);
//            playIntent.setAction(Config.ACTION.PLAY_ACTION);
//            PendingIntent pplayIntent = PendingIntent.getService(this, 0,
//                    playIntent, 0);
//
//            Intent nextIntent = new Intent(this, ForegroundService.class);
//            nextIntent.setAction(Config.ACTION.NEXT_ACTION);
//            PendingIntent pnextIntent = PendingIntent.getService(this, 0,
//                    nextIntent, 0);
//
//            Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.ic_launcher);
//
//            /** Set up notification with pendingIntent **/
//            Notification notification = new NotificationCompat.Builder(this)
//                    .setContentTitle("Podcast Player")
//                    .setTicker("Podcast Player")
//                    .setContentText("My Episode")
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setLargeIcon(
//                            Bitmap.createScaledBitmap(icon, 128, 128, false))
//                    .setContentIntent(pendingIntent)
//                    .setOngoing(true)
//                    .addAction(android.R.drawable.ic_media_previous,
//                            "Previous", ppreviousIntent)
//                    .addAction(android.R.drawable.ic_media_play, "Play",
//                            pplayIntent)
//                    .addAction(android.R.drawable.ic_media_next, "Next",
//                            pnextIntent).build();
//
//            // transform normal service into foreground service (id = 101)
//            startForeground(Config.NOTIFICATION_ID.FOREGROUND_SERVICE,
//                    notification);
//
//        } else if (intent.getAction().equals(Config.ACTION.PREV_ACTION)) {
//            Log.d(Config.DEBUG_TAG, "Clicked Previous");
//
//
//        } else if (intent.getAction().equals(Config.ACTION.PLAY_ACTION)) {
//            Log.d(Config.DEBUG_TAG, "Clicked Play");
//
//
//        } else if (intent.getAction().equals(Config.ACTION.NEXT_ACTION)) {
//            Log.d(Config.DEBUG_TAG, "Clicked Next");
//
//        } else if (intent.getAction().equals(Config.ACTION.STOPFOREGROUND_ACTION)) {
//            Log.d(Config.DEBUG_TAG, "Received Stop Foreground Intent");
//
//            // Does NOT stop service, only removes from foreground - call stopSelf!
//            stopForeground(true);
//            stopSelf();
//        }
//
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(Config.DEBUG_TAG, "In onDestroy");
//    }
//
//
//    // Used only in bound services
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//}
