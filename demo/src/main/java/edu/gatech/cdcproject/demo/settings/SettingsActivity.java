package edu.gatech.cdcproject.demo.settings;

import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.gatech.cdcproject.demo.R;


//Log in part which can be moved to Service in the future
public class SettingsActivity extends AppCompatActivity {
    private Button logInButton;
    public static Firebase myFirebaseRef= new Firebase("https://sizzling-fire-2230.firebaseio.com/");;
    public static String ID;
    private EditText editText_0;
    private EditText editText_1;
    String personal_info;
    private TextView healthInfo;
    private Button logOutBtn;

    protected ValueEventListener myVEListenner = new ValueEventListener() {
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
    };

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

    @Override
    protected void onStop() {
        super.onStop();
        if(ID != null){
            myFirebaseRef.child(ID+"").removeEventListener(myVEListenner);
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

        if(personal_info==null) {
            String dataURL = "http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base/Patient/" + ID + "?_format=json";
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... dataurls) {
                    try {
                        return sendGet(dataurls[0]);
                    } catch (Exception e) {
                        return "error " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    JSONObject myJO;
                    try {
                        myJO = new JSONObject(result);
                        personal_info = parseJsonObject(myJO);
                        healthInfo.setText(personal_info);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.execute(dataURL);
        }
        else
            healthInfo.setText(personal_info);
    }

    private void setupUI() {
        // Toolbar
        editText_0 = (EditText) findViewById(R.id.usernameText);
        editText_1 = (EditText) findViewById(R.id.passwordText);

        editText_0.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus)
                                editText_0.setText("");
                    }
                }
        );

        editText_1.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus)
                                editText_1.setText("");
                    }
                }
        );

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
        personal_info=null;
        Toast.makeText(getApplicationContext(),"Successfully log out.", Toast.LENGTH_SHORT).show();
        finish();
        //这里log out之后结束，就要求在别的fragment中加入re-layout的功能，比如 onResume（）
    }

    public void myLogin(View v){
        myFirebaseRef.child(editText_0.getText().toString()).addValueEventListener(myVEListenner);
    }

    private String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }

    private String parseJsonObject(JSONObject myJo)
    {
        String info="";
        try {
            JSONArray name=myJo.getJSONArray("name");
            String family=name.getJSONObject(0).getJSONArray("family").getString(0);
            String given=name.getJSONObject(0).getJSONArray("given").getString(0);
            String gender=myJo.getString("gender");
            String birthDate=myJo.getString("birthDate");
            info+="Name: "+given+" "+family+"\n\n";
            info+="Gender: "+gender+"\n\n";
            info+="Birth date: "+birthDate+"\n";
            return info;
        }
        catch(Exception e)
        {
        }
        return "";
    }

    /*
    {    "resourceType":"Patient",
            "id":"88",
            "text":{
        "status":"generated",
                "div":"<div><div class=\"hapiHeaderText\"> Thaddeus E <b>HAYNES </b></div><table class=\"hapiPropertyTable\"><tbody><tr><td>Address</td><td><span>668 Laura Circle </span><br /><span></span><br /><span>Atlanta </span><span>GA </span></td></tr><tr><td>Date of birth</td><td><span>15 October 1966</span></td></tr></tbody></table></div>"    },
        "active":true,
            "name":[
        {            "family":[
            "Haynes"            ],
            "given":[
            "Thaddeus",
                    "E"            ]        }    ],
        "gender":"male",    "birthDate":"1966-10-15",
            "address":[        {            "use":"home",
            "line":[                "668 Laura Circle"
        ],            "city":"Atlanta",
                "state":"GA",
                "postalCode":"30301"        }    ]}
    */
}

