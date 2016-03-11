package com.clarifai.androidstarter;

import android.app.*;
import android.os.*;
import android.text.method.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
//import android.widget.EditText;
//import android.widget.TextView;

/**
 * Created by Chutian on 2016/3/10.
 */
public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity" ;

    Button loginButton;
    EditText username_edit;
    EditText passwd_edit;
    TextView infoWords;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loginactivity);

        username_edit = (EditText)findViewById(R.id.username_edit);
        passwd_edit  =  (EditText)findViewById(R.id.passwd_edit);
        infoWords    = (TextView)findViewById(R.id.infowords);
        infoWords.setMovementMethod(LinkMovementMethod.getInstance());

        loginButton =(Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=username_edit.getText().toString().trim();
                String passwd=passwd_edit.getText().toString().trim();
                if(checkAcount(user,passwd)){
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this, "用户名密码不正确，请重新输入！", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    boolean checkAcount(String user,String passwd){
        Log.w(TAG, "checkAcconut " + user + passwd);
        if(user == "test" && passwd == user){
            return true;
        }
        return false;
    }
}
