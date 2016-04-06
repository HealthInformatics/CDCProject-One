package com.clarifai.androidstarter;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;

import com.clarifai.androidstarter.R;

import java.util.ArrayList;

public class confirm_page extends Activity {

    private TextView textview;
    private Button cancel_button;
    private Button upload_button;
    private ListView listView;
    private CheckBox fruit_and_veggie;
    private CheckBox sweet_drink;
    private CheckBox fruit_drink;
    private String data_from_recognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);
        textview=(TextView)findViewById(R.id.textView);

        cancel_button=(Button) findViewById(R.id.cancel_data);
        upload_button=(Button) findViewById(R.id.upload_data);
        fruit_and_veggie=(CheckBox) findViewById(R.id.fruit_and_veggie);
        sweet_drink=(CheckBox) findViewById(R.id.sweet_drink);
        fruit_drink=(CheckBox) findViewById(R.id.fruit_drink);


        Intent myIntent = getIntent();
        ArrayList<String> data = myIntent.getStringArrayListExtra("data");

        data_from_recognition="";
        for(String s:data)
        {
            data_from_recognition=data_from_recognition+"\n"+s;
        }
        textview.setText(data_from_recognition);


        cancel_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        upload_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if(fruit_drink.isChecked())
                            data_from_recognition=fruit_drink.getText()+"\n"+data_from_recognition;
                        if(fruit_and_veggie.isChecked())
                            data_from_recognition=fruit_and_veggie.getText()+"\n"+data_from_recognition;
                        if(sweet_drink.isChecked())
                            data_from_recognition=sweet_drink.getText()+"\n"+data_from_recognition;

                        textview.setText(data_from_recognition);
                    }
                }
        );
    }



}
