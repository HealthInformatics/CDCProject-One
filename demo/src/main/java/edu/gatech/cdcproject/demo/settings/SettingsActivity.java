package edu.gatech.cdcproject.demo.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import edu.gatech.cdcproject.demo.R;




public class SettingsActivity extends AppCompatActivity {
    private Button logInButton;
    public static Firebase myFirebaseRef= new Firebase("https://sizzling-fire-2230.firebaseio.com/");;
    public static String ID;
    private EditText editText_0;
    private EditText editText_1;

    private TextView healthInfo;
    private Button logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ID == null){
            setContentView(R.layout.activity_settings);
            setupUI();
        }else{
            setContentView(R.layout.activity_settings_2);
            setupUI_2();
        }
    }

    private void setupUI_2() {
        // Toolbar
        healthInfo = (TextView) findViewById(R.id.textView);
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
        logOutBtn = (Button) findViewById(R.id.button);
    }

    /*
    protected void onResume(){
        super.onResume();
        if(ID == null){
            setContentView(R.layout.activity_settings);
            setupUI();
        }else{
            setContentView(R.layout.activity_settings_2);
        }
    }
*/

    private void setupUI() {
        // Toolbar
        editText_0 = (EditText) findViewById(R.id.usernameText);
        editText_1 = (EditText) findViewById(R.id.passwordText);
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

    public void myLogout(View v){
        ID = null;
        Toast.makeText(getApplicationContext(),"Successfully log out.", Toast.LENGTH_SHORT).show();
        finish();
        //这里log out之后结束，就要求在别的fragment中加入re-layout的功能，比如 onResume（）
    }



    public void myLogin(View v){

        myFirebaseRef.child(editText_0.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    if (editText_1.getText().toString().equals(snapshot.child("PW").getValue().toString())) {
                        ID = editText_0.getText().toString();
                        Toast.makeText(getApplicationContext(), "Hello, User " + ID, Toast.LENGTH_SHORT).show();
                        //SettingsActivity.this.onBackPressed();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Wrong password.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Account does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(FirebaseError error) {
                Toast.makeText(getApplicationContext(),"Cannot log in.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
