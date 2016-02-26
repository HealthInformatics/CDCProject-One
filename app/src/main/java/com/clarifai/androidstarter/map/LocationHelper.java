package com.clarifai.androidstarter.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.clarifai.androidstarter.constants.RequestCodes;

/**
 * Created by guoweidong on 2/24/16.
 */
public class LocationHelper {
    public static final String TAG = LocationHelper.class.getName();

    private Context context;
    public LocationHelper(Context context) {
        this.context = context;
    }

    public Location getCurrentLocation() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // get location from network provider or gps provider
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return location;
    }
}
