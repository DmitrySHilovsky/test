package com.example.test;

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
import java.time.Instant;

public class MyForegroundService extends Service {

    public static final int SHORT_VIBRATION_DURATION = 200; // Длительность вибрации в миллисекундах
    public static final int LONG_VIBRATION_DURATION = 600; // Длительность вибрации в миллисекундах
    public static final String SERVER_IP = "51.77.116.226"; // IP - СЕРВЕРА
    public static final int SERVER_PORT = 29996; // ПОРТ СЕРВЕРА

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static Integer counter = 0; // 4 байта: порядковый номер сообщения counter(Int)
    // Структура сообщений на отправку:
    public static String identificator = "AA"; // 2 байта: identificator, по умолчанию «00»
    private final Duration LONG_PRESSING_TIME = Duration.ofMillis(300); // Значение в миллисекундах после которого нажатие считается долгим
    private Instant buttonReleaseTime;
    private Instant buttonPressTime;
    private Duration buttonPressDuration;
    private Short typeButton = 00;
    private Short typeDuration = 00;
    private final String message = "0"; // 1-8 байт: сообщение, по умолчанию «0»
    private Vibrator vibrator;
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        UdpSender udpSender = new UdpSender(this);
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
                        try {
                            if (direction == 1) {
                                buttonPressTime = Instant.now();
                                typeButton = 1;
                                // TODO: отправка сообщения тайп 1 лонг 0
                                UdpSender.sendPacket(identificator, typeDuration, typeButton, message);
                            }

                            if (direction == -1) {
                                buttonPressTime = Instant.now();
                                typeButton = 0;
                                // TODO: отправка тайп 0 лонг 0
                                UdpSender.sendPacket(identificator, typeDuration, typeButton, message);
                            }

                            if (direction == 0) {
                                buttonReleaseTime = Instant.now();
                                buttonPressDuration = Duration.between(buttonPressTime, buttonReleaseTime);
                                if (buttonPressDuration.toMillis() > LONG_PRESSING_TIME.toMillis()) {
                                    typeDuration = 1;
                                    // TODO: отправка сообщения лонг 1 тайп = typeButton
                                    UdpSender.sendPacket(identificator, typeDuration, typeButton, message);
                                } else {
                                    typeDuration = 0;
                                }
                                // todo: убрать отсюда
                                // Log.d("MESSAGE", ": " + "id: " + identificator + " " + "type:" + typeDuration + typeButton + " " + "count: " + counter + " " + "message: " + message);


                            }
                        } catch (IOException e) {
                            Log.d("ERROR_UDP", e.getMessage());
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
        Log.d("onDestroy", "DESTROY");
        mediaSession.setActive(false);
        stopForeground(true);
        stopSelf();
    }
}











