package com.clarifai.androidstarter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;

public class BMI extends Activity {

    private Button calculate;
    private TextView result;
    private EditText weight;
    private EditText height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        weight=(EditText) findViewById(R.id.input_weight);
        height=(EditText) findViewById(R.id.input_height);
        result=(TextView) findViewById(R.id.result);
        calculate=(Button) findViewById(R.id.calculate);
        calculate.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        cal_BMI();
                    }
                }
        );
    }

    private void cal_BMI()
    {
        String w = weight.getText().toString();
        String h= height.getText().toString();
        float f_w = Float.parseFloat(w);
        float f_h=Float.parseFloat(h);
        float f_r=f_w/(f_h*f_h);
        String res=Float.toString(f_r);
        if(f_r<18.5)
            res+="\nYou are too thin! EAT EAT EAT";
        else if(f_r<24.9)
            res+="\nYou are good! Keep working";
        else if(f_r<29.9)
            res+="\nDude, you are overweight! You need some self control";
        else
            res+="\nOMG, you are so fat!";

        result.setText(res);
    }




}
