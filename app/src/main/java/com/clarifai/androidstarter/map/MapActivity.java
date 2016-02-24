package com.clarifai.androidstarter.map;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.clarifai.androidstarter.R;

/**
 * @MapActivity: Activity used to find nearby recreation facilities
 */
public class MapActivity extends AppCompatActivity {

    public static final String TAG = MapActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }
}
