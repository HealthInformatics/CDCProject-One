package edu.gatech.cdcproject.demo.foodidentify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.about.AboutActivity;
import edu.gatech.cdcproject.demo.community.CommunityFragment;
import edu.gatech.cdcproject.demo.settings.SettingsActivity;

public class confirm_page extends AppCompatActivity {

    private ListView result_view;
    private ArrayAdapter adapter;

    private Button cancel_button;
    private Button upload_button;
    private CheckBox fruit_and_veggie;
    private CheckBox sweet_drink;
    private CheckBox fruit_drink;
    private String data_from_recognition;
    private ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);



        result_view=(ListView)findViewById(R.id.result_view);


        cancel_button=(Button) findViewById(R.id.cancel_data);
        upload_button=(Button) findViewById(R.id.upload_data);
        fruit_and_veggie=(CheckBox) findViewById(R.id.fruit_and_veggie);
        sweet_drink=(CheckBox) findViewById(R.id.sweet_drink);
        fruit_drink=(CheckBox) findViewById(R.id.fruit_drink);


        Intent myIntent = getIntent();
        data = myIntent.getStringArrayListExtra("data");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data);


        result_view.setAdapter(adapter);


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
                            data.add(0,fruit_drink.getText().toString());
                        if(fruit_and_veggie.isChecked())
                            data.add(0,fruit_and_veggie.getText().toString());
                        if(sweet_drink.isChecked())
                            data.add(0,sweet_drink.getText().toString());

                        adapter.notifyDataSetChanged();


                        Calendar cal = Calendar.getInstance();
                        String date=""+cal.getTime();

                        if(SettingsActivity.ID!=null)
                            SettingsActivity.myFirebaseRef.child(SettingsActivity.ID).child("food").child(date).setValue(data);
                        else {
                            Intent login_activity = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(login_activity);
                        }
                        finish();

                    }
                }
        );
    }



}
