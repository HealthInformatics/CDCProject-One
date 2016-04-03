package com.clarifai.androidstarter;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.clarifai.androidstarter.R;

import java.util.ArrayList;

public class confirm_page extends Activity {

    private TextView textview;
    private Button cancel_button;
    private Button upload_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);
        textview=(TextView)findViewById(R.id.confirm_data);
        cancel_button=(Button) findViewById(R.id.cancel_data);
        upload_button=(Button) findViewById(R.id.upload_data);


        Intent myIntent = getIntent();
        ArrayList<String> data = myIntent.getExtras().getParcelable("data");
        //textview.setText(data);

        cancel_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );

        upload_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );
    }

}
