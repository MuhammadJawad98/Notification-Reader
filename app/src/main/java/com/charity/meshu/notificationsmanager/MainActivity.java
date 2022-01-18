package com.charity.meshu.notificationsmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech t1;
    Button btn;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btnSpeak_id);

        t1 = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
//                String languageToLoad  = "wi"; // your language
//                Locale locale = new Locale(languageToLoad);
//                Locale.setDefault(locale);

                t1.setLanguage(new Locale("iw_IL"));
//                t1.setLanguage(Locale.forLanguageTag("he"));
//                t1.isLanguageAvailable(new Locale("iw"));
                t1.setSpeechRate(0.70f);
            }
        });
        btn.setOnClickListener(view -> {
            if (t1 != null) {
                t1.stop();
                t1.shutdown();
            }
        });
        boolean isNotificationServiceRunning = isNotificationServiceRunning();
        if (!isNotificationServiceRunning) {
            Toast.makeText(getApplicationContext(), "please allow notification", Toast.LENGTH_SHORT).show();
//            Uri uri = Uri.fromParts("package", getPackageName(), null);
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        } else {
            LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        }
    }

    @Override
    protected void onDestroy() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onDestroy();
    }

//    public void onPause() {
//        if (t1 != null) {
//            t1.stop();
//            t1.shutdown();
//        }
//        super.onPause();
//    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            Toast.makeText(getApplicationContext(), "you receive a notification" + "\n" + pack + "\n" + text, Toast.LENGTH_SHORT).show();

//            t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    };

    private boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }
}