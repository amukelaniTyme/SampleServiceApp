package com.xpertek.tyme.tymekioskiot;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SampleServiceNative  extends Service {

    private String channelId = "TestChannelId";
    private Thread thread;
    private Toast toast;

    @Override
    public void onCreate() {
        super.onCreate();
        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        startForegroundWithNoti(this, 1, channelId, "ChannelName", getNoti());
    }

    public static void startForegroundWithNoti(Service service, int notifyId, String channelId, CharSequence channelName, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && notificationManager.getNotificationChannel(channelId) == null) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }

        service.startForeground(notifyId, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStartCheckIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification getNoti() {
        return new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup("GROUP_1")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    protected void onStartCheckIntent(Intent intent) {
        toast.setText("Sample Iot Service receive data from Kiosk - isKioskAppForeground: " + (intent != null ? intent.getBooleanExtra(IotServiceConstant.EXTRA, false) : ""));
        toast.show();
        thread = new Thread(() -> {
            try {
                ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    Log.i("vtt %s", service.service.getPackageName());
                }
                Thread.sleep(10000);
                SampleServiceNative.this.stopSelf();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
