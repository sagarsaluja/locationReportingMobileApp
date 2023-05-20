package com.example.locationmodule;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.app.Activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;



public class LocationModule extends ReactContextBaseJavaModule {
    private static final String TAG = "LocationModule";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final long MIN_TIME_BETWEEN_UPDATES = 10000; // 1 second
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000; // 0 meters

    private Promise locationPromise;
    private final ReactApplicationContext reactContext;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        locationManager = (LocationManager) reactContext.getSystemService(reactContext.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged called LocationModule");
                sendLocationUpdate(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged called LocationModule");
                // Handle status changes if needed
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled called LocationModule");
                // Handle provider enabled if needed
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled called LocationModule");
                // Handle provider disabled if needed
            }
        };
    }

       @Override
    public String getName() {
           Log.d(TAG, "getName called LocationModule");
        return "LocationModule";
    }

    @ReactMethod
    public void startLocationUpdates() {
        //this method is exposed to js. 
        Log.d(TAG, "startLocationUpdates called LocationModule");
        // Check for location permission
        if (ContextCompat.checkSelfPermission(reactContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        // Start location updates
        try {
            Log.d(TAG, "attempting to start location updates");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            // You can also request updates from other providers like NETWORK_PROVIDER if needed
            Log.d(TAG, "Location updates started");
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to start location updates: " + e.getMessage());
        }
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
    }


    private void sendLocationUpdate(Location location) {
        Log.d(TAG, "sendLocationUpdate called LocationModule");
        WritableMap params = Arguments.createMap();
        params.putDouble("latitude", location.getLatitude());
        params.putDouble("longitude", location.getLongitude());

        // Send the location update to JavaScript using the event emitter
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("locationUpdate", params);
    }
    
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivity called LocationModule");
        // Handle the activity result here if needed
    }

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            Log.d(TAG, "onRequestPermissionsResult called LocationModule");
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, resolve the promise with the location
                    if (locationPromise != null) {
                        locationPromise.resolve(null); // Replace `null` with the actual location
                        locationPromise = null;
                    }
                } else {
                    // Permission denied, reject the promise
                    if (locationPromise != null) {
                        locationPromise.reject("PERMISSION_DENIED", "Location permission denied");
                        locationPromise = null;
                    }
                }
            }
        }
    };
}
