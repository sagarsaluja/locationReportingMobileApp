package com.example.locationmodule;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationModule extends ReactContextBaseJavaModule {
    private static final String TAG = "LocationModule";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final long MIN_TIME_BETWEEN_UPDATES = 10000; // 1 second
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000; // 0 meters
    private final ReactApplicationContext reactContext;

    private Promise locationPromise;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private BroadcastReceiver locationUpdateReceiver;

    public LocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        locationManager = (LocationManager) reactContext.getSystemService(reactContext.LOCATION_SERVICE);
    }

    @Override
    public String getName() {
           Log.d(TAG, "getName called LocationModule");
        return "LocationModule";
    }

    @ReactMethod
    public void startLocationUpdates2() {
        // Check for location permission
        if (ContextCompat.checkSelfPermission(reactContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            // Location permission already granted, check notification permission
            if (NotificationManagerCompat.from(reactContext).areNotificationsEnabled()) {
                // Notification permission granted, start the LocationService
                startLocationService();
            } else {
                // Notification permission not granted, request it
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, reactContext.getPackageName());
                reactContext.startActivityForResult(intent, PERMISSION_REQUEST_CODE, null);
            }
        }
    }
    private void startLocationService() {
        // Start the LocationService
        Intent serviceIntent = new Intent(reactContext, LocationService.class);
        reactContext.startService(serviceIntent);
        registerLocationUpdateReceiver();
    }
    @ReactMethod
    public void stopLocationUpdates2(){
        Intent serviceIntent = new Intent(reactContext, LocationService.class);
        reactContext.stopService(serviceIntent);
        unregisterLocationUpdateReceiver();
    }
    private void sendLocationUpdate(double latitude, double longitude) {
        Log.d(TAG, "sendLocationUpdate called LocationModule");
        WritableMap params = Arguments.createMap();
        params.putDouble("latitude", latitude);
        params.putDouble("longitude", longitude);

        // Send the location update to JavaScript using the event emitter
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("LOCATION_UPDATE", params);
    }

    private void registerLocationUpdateReceiver() {
        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive called LocationModule");
                double latitude = intent.getDoubleExtra("latitude", 0.0);
                double longitude = intent.getDoubleExtra("longitude", 0.0);
                Log.d(TAG, "Latitude1: " + latitude + ", Longitude1: " + longitude);
                sendLocationUpdate(latitude , longitude);
            }
        };
        LocalBroadcastManager.getInstance(reactContext).registerReceiver(locationUpdateReceiver, new IntentFilter("LOCATION_UPDATE"));
    }

    private void unregisterLocationUpdateReceiver() {
        if (locationUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(reactContext).unregisterReceiver(locationUpdateReceiver);
            locationUpdateReceiver = null;
        }
    }
}
