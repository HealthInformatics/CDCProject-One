package edu.gatech.cdcproject.demo.healthrecord;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;





import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uhn.fhir.context.FhirContext;
//import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.HTTPVerbEnum;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import edu.gatech.cdcproject.demo.BMI.BMI;
import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.settings.SettingsActivity;


public class HealthRecordFragment extends Fragment {
    private Button hRecord;
    private Button hUpload;

    private ArrayList<Integer> activity_result=new ArrayList<Integer>();

    private GraphView line_graph;

    //private TextView hTextView;
    private Spinner myPALevelSpinner;
    private int isHPLA = 1;
    private EditText myPATime;
    //String[] myPALevel = {"High-level", "Low-level"};
    public String myServerBase = "http://52.72.172.54:8080/fhir/baseDstu2";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_health_record, container, false);

        line_graph=(GraphView) view.findViewById(R.id.line_graph);

        hRecord = (Button) view.findViewById(R.id.get_record);
        hUpload = (Button) view.findViewById(R.id.Upload);
        myPATime = (EditText) view.findViewById(R.id.editText);

        myPALevelSpinner = (Spinner) view.findViewById(R.id.spinner);

        List<String> myPAL = new ArrayList<String>();
        myPAL.add("High-level Activity");
        myPAL.add("Low-level Activity");
        ArrayAdapter<String> myPLAAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, myPAL);
        myPLAAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myPALevelSpinner.setAdapter(myPLAAdapter);
        myPALevelSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (parent.getItemAtPosition(position) == "High-level Activity")
                            isHPLA = 1;
                        else
                            isHPLA = 0;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );


        if(SettingsActivity.ID != null){
            //hTextView.setText("Hello, User " + SettingsActivity.ID + ". To get your personal health information, please click \"retrive\" button.");
            hRecord.setClickable(true);
        }else {
            //hTextView.setText("To view personal health information, please log in.");
            hRecord.setClickable(false);
        }

        hRecord.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {


                        if(SettingsActivity.ID!=null) {
                            SettingsActivity.myFirebaseRef.child(SettingsActivity.ID.toString()).addValueEventListener(myVEListenner);



                        }else {
                            //Toast.makeText(getContext(), "Please login in first", Toast.LENGTH_SHORT).show();
                            Intent login_activity = new Intent(getActivity(), SettingsActivity.class);
                            startActivity(login_activity);
                        }





                        /*String dataURL = "http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base/Patient/" + SettingsActivity.ID + "?_format=json";
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
                                    //String mResponse = myJO.getString("response");
                                    hTextView.setText(myJO.toString());
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }.execute(dataURL);*/
                    }
                });

        hUpload.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        Calendar cal = Calendar.getInstance();
                        String date=""+cal.getTime();

                        if(SettingsActivity.ID!=null) {
                            if(myPATime.getText().toString() == null || isHPLA == 0){
                                Toast.makeText(getActivity(), "Miss data.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Map<String,String> result_value=new HashMap<String,String>();
                            result_value.put("Time",myPATime.getText().toString());
                            result_value.put("IsHighLevel",""+isHPLA);

                            SettingsActivity.myFirebaseRef.child(SettingsActivity.ID).child("Physicial Activity").child(date).setValue(result_value);


                            Fragment fragment = new HealthRecordFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                        }
                        else {
                            //Toast.makeText(getContext(), "Please login in first", Toast.LENGTH_SHORT).show();
                            Intent login_activity = new Intent(getActivity(), SettingsActivity.class);
                            startActivity(login_activity);
                        }

                        /*FhirContext ctx = FhirContext.forDstu2();
                        //String serverBase = "http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base";

                        IGenericClient client = ctx.newRestfulGenericClient(myServerBase);

                        //ca.uhn.fhir.model.dstu2.resource.Bundle results = client
                        //        .search()
                        //        .forResource(Patient.class)
                        //        .where(Patient.FAMILY.matches().value("duck"))
                        //        .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                        //       .execute();
                        //
                        //hTextView.setText("Found " + results.getEntry().size() + " patients named 'duck'");

                        // Create a patient object
                        Patient patient = new Patient();
                        patient.addIdentifier()
                                .setSystem("http://acme.org/mrns")
                                .setValue("12345");
                        patient.addName()
                                .addFamily("Jameson")
                                .addGiven("J")
                                .addGiven("Jonah");
                        patient.setGender(AdministrativeGenderEnum.MALE);

                        // Give the patient a temporary UUID so that other resources in
                        // the transaction can refer to it
                        patient.setId(IdDt.newRandomUuid());

                        // Create an observation object
                        Observation observation = new Observation();
                        observation.setStatus(ObservationStatusEnum.FINAL);
                        observation
                                .getCode()
                                .addCoding()
                                .setSystem("http://loinc.org")
                                .setCode("789-8")
                                .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
                        observation.setValue(
                                new QuantityDt()
                                        .setValue(4.12)
                                        .setUnit("10 trillion/L")
                                        .setSystem("http://unitsofmeasure.org")
                                        .setCode("10*12/L"));


                        // The observation refers to the patient using the ID, which is already
                        // set to a temporary UUID
                        observation.setSubject(new ResourceReferenceDt(patient.getId().getValue()));

                        // Create a bundle that will be used as a transaction
                        ca.uhn.fhir.model.dstu2.resource.Bundle bundle = new ca.uhn.fhir.model.dstu2.resource.Bundle();
                        bundle.setType(BundleTypeEnum.TRANSACTION);

                        // Add the patient as an entry. This entry is a POST with an
                        // If-None-Exist header (conditional create) meaning that it
                        // will only be created if there isn't already a Patient with
                        // the identifier 12345
                        bundle.addEntry()
                                .setFullUrl(patient.getId().getValue())
                                .setResource(patient)
                                .getRequest()
                                .setUrl("Patient")
                                .setIfNoneExist("Patient?identifier=http://acme.org/mrns|12345")
                                .setMethod(HTTPVerbEnum.POST);

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
                        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));*/




                    }
                });
        return view;
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


    protected ValueEventListener myVEListenner = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot != null) {
                if (snapshot.hasChild("Physicial Activity")) {
                    System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!PAPAPA");
                    Iterator myI = snapshot.child("Physicial Activity").getChildren().iterator();


                    while(myI.hasNext()){
                        String myResult = myI.next().toString();
                        activity_result.add(  Integer.parseInt(myResult.substring(myResult.indexOf("Time=")+5).split(",")[0]) );
                    }

                    SettingsActivity.myFirebaseRef.child(SettingsActivity.ID.toString()).removeEventListener(myVEListenner);
                    generateGraph();

                }else{
                    Toast.makeText(getActivity(), "No Activity History.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
            }
        }
        @Override
        public void onCancelled(FirebaseError error) {
        }
    };

    private void generateGraph()
    {

        int size=activity_result.size();
        if(size>5)
            size=5;

        line_graph.removeAllSeries();


        int max=0;
        DataPoint[] data = new DataPoint[size];
        for (int i = 0; i < size; i++) {
            int x=activity_result.get(activity_result.size()-size+i);
            if(x>max)
                max=x;

            data[i] = new DataPoint(i,x);
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(data);
        line_graph.addSeries(series);

        line_graph.getViewport().setMinY(0);
        line_graph.getViewport().setMaxY(max);
        line_graph.getViewport().setYAxisBoundsManual(true);

        line_graph.getViewport().setMinX(0);
        line_graph.getViewport().setMaxX(size-1);
        line_graph.getViewport().setXAxisBoundsManual(true);

        series.setColor(Color.BLUE);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);
        series.setThickness(4);

    }



}
