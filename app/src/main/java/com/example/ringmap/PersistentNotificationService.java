package com.example.ringmap;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.ringmap.R;

import kotlinx.coroutines.Delay;

public class PersistentNotificationService extends Service {

    private static final int NOTIFICATION_ID = 1;
    Ringtone ringtone = null;
    private static final String CHANNEL_ID = "PersistentNotificationChannel";
    private Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private static OnServiceInteractionListener callback1;

    public PersistentNotificationService() {
        // Construtor padrão vazio
    }

    public static void setCallback(OnServiceInteractionListener callback) {
        callback1 = callback;
    }

    // ... Outro código do serviço

    @Override
    public void onCreate() {
        super.onCreate();

        CreateNotificationChannel();

        Notification notification = buildNotification();

        startForeground(NOTIFICATION_ID, notification);
        startLoopingAudio();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void CreateNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Toque do alarme",
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private Notification buildNotification() {
        Intent stopIntent = new Intent(this, PersistentNotificationService.class);
        stopIntent.setAction("STOP_ACTION");
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setOnClickPendingIntent(R.id.btnStop, stopPendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setCustomContentView(remoteViews)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);

        return builder.build();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP_ACTION".equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }


    private void startLoopingAudio() {
        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
            ringtone.setLooping(true);
        }

        ringtone.play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ringtone != null) {
            ringtone.stop();
        }
        if(callback1 != null)
            callback1.onServiceDataReceived("close");

    }
    public interface OnServiceInteractionListener {
        void onServiceDataReceived(String data);
    }

}
