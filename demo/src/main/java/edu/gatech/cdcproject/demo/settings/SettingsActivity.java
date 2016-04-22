package edu.gatech.cdcproject.demo.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import edu.gatech.cdcproject.demo.R;

/**
 * Created by guoweidong on 3/28/16.
 */
public class SettingsActivity extends AppCompatActivity {
    private Button logInButton;
    public static Firebase myFirebaseRef;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupUI();
    }

    private void setupUI() {
        // Toolbar
        editText = (EditText) findViewById(R.id.usernameText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.onBackPressed();
            }
        });
        logInButton = (Button) findViewById(R.id.loginBt);
    }

    public void myLogin(View v){
        myFirebaseRef = new Firebase("https://sizzling-fire-2230.firebaseio.com/");

        myFirebaseRef.child(editText.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println((int)snapshot.child("PW").getValue());
            }
            @Override
            public void onCancelled(FirebaseError error) {
                System.out.print("Cannot login");
            }
        });
    }

}
