package com.cziyeli.podbuddy.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.R;
import com.cziyeli.podbuddy.models.PodcastFav;

import java.io.IOException;

/**
 * Created by connieli on 6/5/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_START_NEW = "action_start_new";

    private MediaPlayer mPodcastPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;

    private String mMediaUrl;
    // TODO: pass in position from intent, get all PodcastFavs
    private PodcastFav[] mMediaQueue;

    public static boolean first_time_through = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( mManager == null ) {
            try {
                initMediaSessions(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        handleIntent( intent );
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            mController.getTransportControls().play();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            mController.getTransportControls().pause();
//        } else if( action.equalsIgnoreCase( ACTION_FAST_FORWARD ) ) {
//            mController.getTransportControls().fastForward();
//        } else if( action.equalsIgnoreCase( ACTION_REWIND ) ) {
//            mController.getTransportControls().rewind();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            mController.getTransportControls().skipToPrevious();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            mController.getTransportControls().skipToNext();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            mController.getTransportControls().stop();
        } else if (action.equalsIgnoreCase ( ACTION_START_NEW )) {
            // 'Listen Latest' click
            startNewPodcast(intent);
            mController.getTransportControls().play();
        }
    }

    // First time initializer
    public void createPodcastPlayer(Intent intent) {
        mPodcastPlayer = new MediaPlayer();
        mMediaUrl = intent.getStringExtra(Config.MEDIA_URL);
        Log.d(Config.DEBUG_TAG, "++ createPodcastPlayer! url: " + mMediaUrl);

        try {
            mPodcastPlayer.setDataSource(mMediaUrl);
            mPodcastPlayer.setOnPreparedListener(this);
//            mPodcastPlayer.setOnCompletionListener(this);
            mPodcastPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Click 'Listen Latest' - reset listen latest **/
    private void startNewPodcast(Intent intent) {
        if (mPodcastPlayer == null) {
            createPodcastPlayer(intent);
        } else { // mManager already exists - reset new url
            mMediaUrl = intent.getStringExtra(Config.MEDIA_URL);
            try {
                if (mPodcastPlayer.isPlaying()) {
                    mPodcastPlayer.stop();
                }
                mPodcastPlayer.reset();
                mPodcastPlayer.setDataSource(mMediaUrl);
                mPodcastPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Notification.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    private void buildNotification( Notification.Action action ) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction( ACTION_STOP );
        PendingIntent stopIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);

        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentTitle("Media Title")
                .setContentText("Media Artist")
                .setDeleteIntent(stopIntent) // sends stop intent when notification is cleared
                .setStyle(style);

        builder.addAction( generateAction( android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS ) );
//        builder.addAction( generateAction( android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND ) );
        builder.addAction( action ); // ACTION_STOP
//        builder.addAction( generateAction( android.R.drawable.ic_media_ff, "Fast Forward", ACTION_FAST_FORWARD ) );
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0,1,2);

        // Post a notification to be shown in the status bar
        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        Notification notification = builder.build();
        notificationManager.notify(1, notification);

//        startForeground(1, notification);
    }


    /** mPodcastPlayer callbacks **/

    public void onPrepared(MediaPlayer mp) {
        if (mp == mPodcastPlayer) {
            mPodcastPlayer.start();
        }
    }

//    public void onCompletion(MediaPlayer mp) {
//        Log.d(Config.DEBUG_TAG, "++ onCompletion!!" + mMediaUrl);
//        mp.stop();
//        mp.release();
//    }

    /** Only called the first time (no Media Manager yet) **/
    private void initMediaSessions(Intent intent) throws IOException {
        createPodcastPlayer(intent);

        /** SET CONTROL CALLBACKS **/

        mManager = (MediaSessionManager)getSystemService(Context.MEDIA_SESSION_SERVICE);
        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mController = new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback() {
                 @Override
                 public void onPlay() {
                     super.onPlay();
                     buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));

                     Log.d(Config.DEBUG_TAG, "onPlay with mMediaUrl: " + mMediaUrl);
                     // Don't call start the first time - prepareAsync may not be ready
                     if(first_time_through == true) {
                         first_time_through = false;
                     } else {
                         try {
                             mPodcastPlayer.start();
                         } catch (IllegalArgumentException e) {
                             Toast.makeText(getApplicationContext(), "IllegalArgumentException", Toast.LENGTH_LONG).show();
                         } catch (SecurityException e) {
                             Toast.makeText(getApplicationContext(), "SecurityException", Toast.LENGTH_LONG).show();
                         } catch (IllegalStateException e) {
                             Toast.makeText(getApplicationContext(), "IllegalStateException", Toast.LENGTH_LONG).show();
                         }
                     }
                 }

                 @Override
                 public void onPause() {
                     super.onPause();
                     if (mPodcastPlayer.isPlaying()) { mPodcastPlayer.pause(); }
                     Log.d(Config.DEBUG_TAG, "onPause with mMediaUrl: " + mMediaUrl);

                     buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                 }

                 @Override
                 public void onSkipToNext() {
                     super.onSkipToNext();
                     Log.d( Config.DEBUG_TAG, "onSkipToNext");

                     // Switch Media Url to next in mMediaQueue

                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                 }

                 @Override
                 public void onSkipToPrevious() {
                     super.onSkipToPrevious();
                     Log.d( Config.DEBUG_TAG, "onSkipToPrevious");

                     // Switch Media Url to previous in mMediaQueue

                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                 }

//                 @Override
//                 public void onFastForward() {
//                     super.onFastForward();
//                     Log.d()( Config.DEBUG_TAG, "onFastForward");
//
//                     //Manipulate current media here
//                 }
//
//                 @Override
//                 public void onRewind() {
//                     super.onRewind();
//                     Log.d()( Config.DEBUG_TAG, "onRewind");
//
//                     //Manipulate current media here
//                 }

                 @Override
                 public void onStop() {
                     super.onStop();
                     Log.d(Config.DEBUG_TAG, "onStop");

                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                     notificationManager.cancel( 1 );
                     Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
                     stopService( intent );
                 }

                 @Override
                 public void onSeekTo(long pos) {
                     super.onSeekTo(pos);
                 }

                 @Override
                 public void onSetRating(Rating rating) {
                     super.onSetRating(rating);
                 }
             }
        );
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSession.release();
        return super.onUnbind(intent);
    }
}
