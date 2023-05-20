package com.locationreporting;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.locationmodule.LocationService;
import android.util.Log;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainActivity extends ReactActivity {

  private static final String CHANNEL_ID = "MyNotificationChannel";
  private static final int NOTIFICATION_ID = 123;
  private static final String PREF_NOTIFICATION_PERMISSION_ASKED = "notification_permission_asked";

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "LocationReporting";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. Here we use a util class {@link
   * DefaultReactActivityDelegate} which allows you to easily enable Fabric and Concurrent React
   * (aka React 18) with two boolean flags.
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new DefaultReactActivityDelegate(
            this,
            getMainComponentName(),
            // If you opted-in for the New Architecture, we enable the Fabric Renderer.
            DefaultNewArchitectureEntryPoint.getFabricEnabled(), // fabricEnabled
            // If you opted-in for the New Architecture, we enable Concurrent React (i.e. React 18).
            DefaultNewArchitectureEntryPoint.getConcurrentReactEnabled() // concurrentRootEnabled
    );
  }

  @Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);

  // Check if the app has notification permission
  if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
    // App doesn't have notification permission, ask for it
    askForNotificationPermission();
  }
}


  private void askForNotificationPermission() {
    Log.d("MainActivity", "askForNotificationPermission called");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Create a notification channel
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
      notificationManager.createNotificationChannel(channel);
    }

    // Build a notification to ask for permission
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle("Notification Permission")
            .setContentText("Please grant permission to receive notifications.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    // Open the app settings when the notification is clicked
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.parse("package:" + getPackageName()));
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    builder.setContentIntent(pendingIntent);

    // Show the notification
    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
    notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = "My Channel";
      String description = "Notification Channel";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
      channel.setDescription(description);

      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private boolean getNotificationPermissionAsked() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    return prefs.getBoolean(PREF_NOTIFICATION_PERMISSION_ASKED, false);
  }

  private void setNotificationPermissionAsked(boolean asked) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    prefs.edit().putBoolean(PREF_NOTIFICATION_PERMISSION_ASKED, asked).apply();
  }
}
