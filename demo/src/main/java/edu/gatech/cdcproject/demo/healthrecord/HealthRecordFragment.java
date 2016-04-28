package edu.gatech.cdcproject.demo.healthrecord;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.settings.SettingsActivity;


public class HealthRecordFragment extends Fragment {
    private Button hRecord;
    private TextView hTextView;
    public String serverBase = "http://52.72.172.54:8080/fhir/baseDstu2";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        System.out.println("onCreate!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        View view = inflater.inflate(R.layout.fragment_health_record, container, false);
        hTextView = (TextView)view.findViewById(R.id.return_record);
        hRecord = (Button) view.findViewById(R.id.get_record);

        if(SettingsActivity.ID != null){
            hTextView.setText("Hello, User " + SettingsActivity.ID + ". To get your personal health information, please click \"retrive\" button.");
            hRecord.setClickable(true);
        }else {
            hTextView.setText("To view personal health information, please log in.");
            hRecord.setClickable(false);
        }

        hRecord.setOnClickListener(
                new View.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("On Click!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    // Create a client to talk to the HeathIntersections server
                    FhirContext ctx = FhirContext.forDstu2();
                    IGenericClient client = ctx.newRestfulGenericClient(serverBase);
                    client.registerInterceptor(new LoggingInterceptor(true));

                    Bundle results = client
                            .search()
                            .forResource(Patient.class)
                            .where(Patient.FAMILY.matches().value("duck"))
                            .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                            .execute();

                    System.out.println("Found " + results.getEntry().size() + " patients named 'duck'");

                    Patient patient = new Patient();
                    // ..populate the patient object..
                    patient.addIdentifier().setSystem("urn:system").setValue("12345");
                    patient.addName().addFamily("Smith").addGiven("John");

                    // Invoke the server create method (and send pretty-printed JSON
                    // encoding to the server
                    // instead of the default which is non-pretty printed XML)
                    MethodOutcome outcome = client.create()
                            .resource(patient)
                            .prettyPrint()
                            .encodedJson()
                            .execute();

                    // The MethodOutcome object will contain information about the
                    // response from the server, including the ID of the created
                    // resource, the OperationOutcome response, etc. (assuming that
                    // any of these things were provided by the server! They may not
                    // always be)
                    IdDt id = (IdDt) outcome.getId();
                    System.out.println("Got ID: " + id.getValue());


                String dataURL="http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base/Patient/" + SettingsActivity.ID +"?_format=json";

                new AsyncTask<String, Void, String>()
                {
                    @Override protected String doInBackground(String... dataurls)
                    {
                        try {
                            return sendGet(dataurls[0]);
                        }
                        catch(Exception e)
                        {
                            return "error "+e.getMessage();
                        }
                    }
                    @Override protected void onPostExecute(String result)
                    {
                        JSONObject myJO;
                        try {
                            myJO = new JSONObject(result);
                            String mResponse = myJO.getString("response");
                            hTextView.setText(mResponse);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }.execute(dataURL);
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
}
