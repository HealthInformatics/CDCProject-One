package edu.gatech.cdcproject.demo.BMI;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.BuildConfig;

public class BMI extends Fragment
{

    private Button calculate;
    private TextView result;
    private EditText weight;
    private EditText height;
    private Spinner weight_spinner;
    private Spinner height_spinner;
    private boolean isKg=false;
    private boolean isM=true;
    private float BMI_value;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_bmi, container, false);

        weight = (EditText) view.findViewById(R.id.input_weight);
        height = (EditText) view.findViewById(R.id.input_height);
        result=(TextView) view.findViewById(R.id.result);
        calculate = (Button) view.findViewById(R.id.calculate);
        calculate.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        cal_BMI();
                        Firebase myFirebaseRef = new Firebase("https://glaring-fire-1928.firebaseio.com/");

                        Calendar cal = Calendar.getInstance();
                        String date=""+cal.getTime();

                        Map<String,String> result_value=new HashMap<String,String>();
                        result_value.put("Height",height.getText().toString()+height_spinner.getSelectedItem());
                        result_value.put("Weight",weight.getText().toString()+weight_spinner.getSelectedItem());
                        result_value.put("BMI", Float.toString(BMI_value));

                        myFirebaseRef.child("BMI").child(date).setValue(result_value);
                    }
                }
        );

        height_spinner=(Spinner) view.findViewById(R.id.height_spinner);
        weight_spinner=(Spinner) view.findViewById(R.id.weight_spinner);

        List<String> weight_option=new ArrayList<String>();
        weight_option.add("lb");
        weight_option.add("kg");
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, weight_option);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weight_spinner.setAdapter(weightAdapter);

        List<String> height_option=new ArrayList<String>();
        height_option.add("m");
        height_option.add("foot");
        ArrayAdapter<String> heightAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, height_option);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        height_spinner.setAdapter(heightAdapter);


        height_spinner.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(parent.getItemAtPosition(position)=="m")
                            isM=true;
                        else
                            isM=false;

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        weight_spinner.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(parent.getItemAtPosition(position)=="kg")
                            isKg=true;
                        else
                            isKg=false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        return view;
    }

    private void cal_BMI()
    {
        String w = weight.getText().toString();
        String h= height.getText().toString();
        float f_w = Float.parseFloat(w);
        float f_h=Float.parseFloat(h);
        if(!isKg)
            f_w=((float)0.453592)*f_w;
        if(!isM)
            f_h=((float)0.3048)*f_h;
        float f_r=f_w/(f_h*f_h);
        BMI_value=f_r;
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
