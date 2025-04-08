package ru.mirea.soldatenkovaka.notificationapp;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {
    private int PermissionCode = 200;

    private static final String CHANNEL_ID = "ru.mirea.soldatenkova.notificationapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.d(MainActivity.class.getSimpleName(), "Разрешения получены");
        } else {
            Log.d(MainActivity.class.getSimpleName(), "Нет разрешений!");
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, PermissionCode);
        }


    }



    public void onClickSendNotification(View view) {
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("Congratulation!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Солдатенкова Ксения Александровна БИСО-03-20"))
                .setContentTitle("МИРЭА");
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Soldatenkova K.A. Notification", importance);
                channel.setDescription("MIREA Channel");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(MainActivity.class.getSimpleName(), "Разрешения получены");
            } else {
                Log.d(MainActivity.class.getSimpleName(), "Нет разрешений!");
            }
        }
    }
}