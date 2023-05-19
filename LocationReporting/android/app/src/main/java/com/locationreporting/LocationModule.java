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
                Log.d(TAG, "onLocationChanged");
                sendLocationUpdate(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle status changes if needed
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Handle provider enabled if needed
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Handle provider disabled if needed
            }
        };
    }

       @Override
    public String getName() {
        return "LocationModule";
    }

    @ReactMethod
    public void startLocationUpdates() {
        // Check for location permission
        if (ContextCompat.checkSelfPermission(reactContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        // Start location updates
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            // You can also request updates from other providers like NETWORK_PROVIDER if needed
            Log.d(TAG, "Location updates started");
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to start location updates: " + e.getMessage());
        }
    }

    private void sendLocationUpdate(Location location) {
        WritableMap params = Arguments.createMap();
        params.putDouble("latitude", location.getLatitude());
        params.putDouble("longitude", location.getLongitude());

        // Send the location update to JavaScript using the event emitter
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("locationUpdate", params);
    }
    
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        // Handle the activity result here if needed
    }

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
