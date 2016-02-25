package com.clarifai.androidstarter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clarifai.androidstarter.constants.RequestCodes;
import com.clarifai.androidstarter.map.LocationHelper;


//This is the main activity.
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = MainActivity.class.getName();

    private Button selectButton;
    private Button searchMapBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectButton = (Button) findViewById(R.id.upload_image);
        searchMapBtn = (Button) findViewById(R.id.searchMapBtn);
        searchMapBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchMapBtn:
                LocationHelper helper = new LocationHelper(this);
                startGoogleMap(helper.getCurrentLocation(), "recreation");
                break;
        }
    }

    private void startGoogleMap(Location location, String query) {
        if(location == null) {
            Toast.makeText(this, "Please open your location service.", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude()
                + "," + location.getLongitude()
                + "?q=" + query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void start_upload_image(View view)
    {
        Intent intent = new Intent(this, RecognitionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.PERMISSION_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    LocationHelper helper = new LocationHelper(this);
                    startGoogleMap(helper.getCurrentLocation(), "recreation");
                    break;

                } else {
                    Log.i(TAG, "User declined the permission.");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
