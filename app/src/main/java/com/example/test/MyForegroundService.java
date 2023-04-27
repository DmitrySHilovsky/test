package com.example.test;

import static com.example.test.UdpSender.sendPacket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.VolumeProviderCompat;

import java.io.IOException;
import java.time.Duration;
import java.util.Timer;
import java.time.Instant;


public class MyForegroundService extends Service {
    Instant buttonReleaseTime;
    Instant buttonPressTime;
    Duration buttonPressDuration;

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int VIBRATION_DURATION = 200; // Длительность вибрации в миллисекундах

    private Timer timer;
    private Vibrator vibrator;

    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        startForeground(1, createNotification()); // Создание уведомления для Foreground Service
        mediaSession = new MediaSessionCompat(this, "PlayerService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0) //you simulate a player which plays something.
                .build());

        //this will only work on Lollipop and up, see https://code.google.com/p/android/issues/detail?id=224134
        VolumeProviderCompat myVolumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, /*max volume*/100, /*initial volume level*/50) {
                    @Override
                    public void onAdjustVolume(int direction) {

                        if (direction == 1) {
                            buttonPressTime = Instant.now();
                            Log.d("VOLUME UP", "НАЖАТИЕ "+buttonPressTime);
                        }

                        if (direction == -1) {
                            buttonPressTime = Instant.now();
                            Log.d("VOLUME DOWN", "НАЖАТИЕ "+buttonPressTime);
                        }

                        if (direction == 0) {
                            buttonReleaseTime = Instant.now();
                            Log.d("VOLUME DOWN/UP", "ОТЖАТИЕ " + buttonReleaseTime);
                            buttonPressDuration = Duration.between(buttonPressTime, buttonReleaseTime);
                            Log.d("VOLUME DOWN/UP","ДИТЕЛЬНОСТЬ: " + buttonPressDuration);
                        }
                    }
                };

        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        String notificationTitle = "Foreground Service";
        String notificationText = "Служба запущена";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}











