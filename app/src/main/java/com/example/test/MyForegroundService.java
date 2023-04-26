package com.example.test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import java.util.Timer;
import java.util.TimerTask;

public class MyForegroundService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int VIBRATION_DURATION = 200; // Длительность вибрации в миллисекундах
    private Timer timer;
    private Vibrator vibrator;

    private BroadcastReceiver volumeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event != null && event.getAction() == KeyEvent.ACTION_UP) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                        Log.d("qqqqqqqqqqqqqq","aaaaaaaaaaaaaaaaaaaaaaa");
                        vibrator.vibrate(1000);
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, createNotification()); // Создание уведомления для Foreground Service
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Вызывается при запуске службы
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        registerReceiver(volumeChangeReceiver, filter);
        startVibration(); // Запуск вибрации каждые 10 секунд
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

    private void startVibration() {
        timer = new Timer();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(VIBRATION_DURATION);
                }
            }
        }, 0, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVibration();
        unregisterReceiver(volumeChangeReceiver);
    }

    private void stopVibration() {
        if (timer != null) {
            timer.cancel();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}











