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
import com.google.android.gms.plus.PlusShare;


//This is the main activity.
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = MainActivity.class.getName();

    private Button selectButton;
    private Button searchMapBtn;
    private Button shareButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectButton = (Button) findViewById(R.id.upload_image);
        searchMapBtn = (Button) findViewById(R.id.searchMapBtn);
        shareButton = (Button) findViewById(R.id.shareButton);
        searchMapBtn.setOnClickListener(this);
        shareButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchMapBtn:
                LocationHelper helper = new LocationHelper(this);
                startGoogleMap(helper.getCurrentLocation(), "recreation");
                break;
            case R.id.shareButton:
                PlusShare.Builder builder = new PlusShare.Builder(this);

                // Set call-to-action metadata.
                builder.addCallToAction(
                        "CREATE_ITEM", /** call-to-action button label */
                        Uri.parse("http://plus.google.com/pages/create"), /** call-to-action url (for desktop use) */
                        "/pages/create" /** call to action deep-link ID (for mobile use), 512 characters or fewer */);

                // Set the content url (for desktop use).
                builder.setContentUrl(Uri.parse("https://plus.google.com/pages/"));

                // Set the target deep-link ID (for mobile use).
                builder.setContentDeepLinkId("/pages/",
                        null, null, null);

                // Set the share text.
                builder.setText("Create your Google+ Page too!" +
                        "<https://github.com/Peleus1992/Lost_and_Found_Android_app/blob/master/screenshot/Screenshot_1.png>");
                startActivityForResult(builder.getIntent(), 0);
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
}
