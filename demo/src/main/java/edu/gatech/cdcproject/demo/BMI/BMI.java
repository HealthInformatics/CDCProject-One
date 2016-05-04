package edu.gatech.cdcproject.demo.BMI;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.HTTPVerbEnum;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.client.IGenericClient;
import edu.gatech.cdcproject.backend.myApi.model.FoodImage;
import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.BuildConfig;
import edu.gatech.cdcproject.demo.community.CommunityFragment;
import edu.gatech.cdcproject.demo.foodidentify.confirm_page;
import edu.gatech.cdcproject.demo.settings.SettingsActivity;
import edu.gatech.cdcproject.demo.ui.MainActivity;
import edu.gatech.cdcproject.demo.util.TimeUtils;

import static edu.gatech.cdcproject.demo.network.Api.myApi;

public class BMI extends Fragment
{

    private ImageButton calculate;
    private ImageButton upload;
    private TextView result;
    private EditText weight;
    private EditText height;
    private Spinner weight_spinner;
    private Spinner height_spinner;
    private boolean isKg=false;
    private boolean isM=true;
    private float BMI_value;
    private ImageView imageView;
    private ProgressBar wait_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_bmi, container, false);

        weight = (EditText) view.findViewById(R.id.input_weight);
        height = (EditText) view.findViewById(R.id.input_height);
        result=(TextView) view.findViewById(R.id.result);
        calculate = (ImageButton) view.findViewById(R.id.calculate);
        upload=(ImageButton) view.findViewById(R.id.upload);

        wait_bar=(ProgressBar) view.findViewById(R.id.progressBar2);

        imageView=(ImageView) view.findViewById(R.id.suggestion);

        imageView.setImageResource(R.drawable.bmichart);

        calculate.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(weight.getText().toString() != null && height.getText().toString() != null) {
                            cal_BMI();
                        }
                    }
                }
        );

        // GT fhir  Observation 30 是关于BMI的
        upload.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {

                        Calendar cal = Calendar.getInstance();
                        String date=""+cal.getTime();

                        //Map<String,String> result_value=new HashMap<String,String>();
                        //result_value.put("Height",height.getText().toString()+height_spinner.getSelectedItem());
                        //result_value.put("Weight",weight.getText().toString()+weight_spinner.getSelectedItem());
                        //result_value.put("BMI", Float.toString(BMI_value));

                        if(SettingsActivity.ID!=null) {

                            new AsyncTask<String, Integer, String>() {

                                @Override
                                protected void onPreExecute() {
                                    wait_bar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                protected String doInBackground(String... params) {
                                    try {



                                        //SettingsActivity.myFirebaseRef.child(SettingsActivity.ID).child("BMI").child(date).setValue(result_value);



                                        FhirContext ctx = FhirContext.forDstu2();
                                        String serverBase = "http://52.72.172.54:8080/fhir/baseDstu2";//"http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base";

                                        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

                                        //ca.uhn.fhir.model.dstu2.resource.Bundle results = client
                                        //        .search()
                                        //        .forResource(Patient.class)
                                        //        .where(Patient.FAMILY.matches().value("duck"))
                                        //        .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                                        //       .execute();
                                        //
                                        //hTextView.setText("Found " + results.getEntry().size() + " patients named 'duck'");


                                        // Create an observation object
                                        Observation observation = new Observation();
                                        observation.setStatus(ObservationStatusEnum.FINAL);
                                        observation
                                                .getCode()
                                                .addCoding()
                                                .setSystem("http://loinc.org")
                                                .setCode("39156-5")
                                                .setDisplay("Body mass index (BMI) [Ratio]");
                                        observation.setValue(
                                                new QuantityDt()
                                                        .setValue(BMI_value)
                                                        .setUnit(params[0] + "/" + params[1])
                                                        .setSystem("http://unitsofmeasure.org")
                                                        .setCode(params[0] + "/" + params[1]));


                                        // The observation refers to the patient using the ID, which is already
                                        // set to a temporary UUID
                                        observation.setSubject(new ResourceReferenceDt(SettingsActivity.ID.toString()));

                                        // Create a bundle that will be used as a transaction
                                        ca.uhn.fhir.model.dstu2.resource.Bundle bundle = new ca.uhn.fhir.model.dstu2.resource.Bundle();
                                        bundle.setType(BundleTypeEnum.TRANSACTION);


                                        // Add the observation. This entry is a POST with no header
                                        // (normal create) meaning that it will be created even if
                                        // a similar resource already exists.
                                        bundle.addEntry()
                                                .setResource(observation)
                                                .getRequest()
                                                .setUrl("Observation")
                                                .setMethod(HTTPVerbEnum.POST);

                                        // Log the request
                                        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle));

                                        // Create a client and post the transaction to the server
                                        ca.uhn.fhir.model.dstu2.resource.Bundle resp = client.transaction().withBundle(bundle).execute();

                                        // Log the response
                                        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));



                                    } catch (Exception e) {
                                    }
                                    return "Completed";
                                }

                                @Override
                                protected void onPostExecute(String result) {
                                    wait_bar.setVisibility(View.GONE);
                                }
                            }.execute(weight_spinner.getSelectedItem().toString(),height_spinner.getSelectedItem().toString());





                            //设置title，现在是调用的static方法
                            //MainActivity.setToolbar(1);//no need to change title anymore. Refresh page and stay in current page
                            Fragment fragment = new BMI();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                        }
                        else {
                            //Toast.makeText(getContext(), "Please login in first", Toast.LENGTH_SHORT).show();
                            Intent login_activity = new Intent(getActivity(), SettingsActivity.class);
                            startActivity(login_activity);
                        }
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
        float f_w;
        float f_h;
        try{
            f_w = Float.parseFloat(w);
            f_h = Float.parseFloat(h);
        }catch(Exception e){
            Toast.makeText(getActivity(),"Miss data.", Toast.LENGTH_SHORT).show();
            return;
        }


        if(!isKg)
            f_w=((float)0.453592)*f_w;
        if(!isM)
            f_h=((float)0.3048)*f_h;
        float f_r=f_w/(f_h*f_h);
        BMI_value=f_r;
        String res=Float.toString(f_r);
        if(f_r<18.5) {
            res += "\nUnderweight\n";
            imageView.setImageResource(R.drawable.highcalories);
        }
        else if(f_r<24.9) {
            res += "\nHealthy Weight\n";
            imageView.setImageResource(R.drawable.keepgoing1);
        }
        else if(f_r<29.9) {
            res += "\nOverweight\n";
            imageView.setImageResource(R.drawable.workout_food);
        }
        else {
            res += "\nObese\n";
            imageView.setImageResource(R.drawable.workout_food);
        }

        result.setText(res);
    }
}
