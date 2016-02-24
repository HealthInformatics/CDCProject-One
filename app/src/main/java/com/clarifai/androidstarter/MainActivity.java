package com.clarifai.androidstarter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



//This is the main activity.
public class MainActivity extends Activity
{

    private Button selectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectButton = (Button) findViewById(R.id.upload_image);

    }


    public void start_upload_image(View view)
    {
        Intent intent = new Intent(this, RecognitionActivity.class);
        startActivity(intent);
    }

}
