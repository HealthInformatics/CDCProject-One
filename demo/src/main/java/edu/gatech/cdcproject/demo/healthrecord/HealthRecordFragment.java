package edu.gatech.cdcproject.demo.healthrecord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
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
//import ca.uhn.fhir.model.dstu2.resource.Bundle;
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
        hTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
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
                                //String mResponse = myJO.getString("response");
                                hTextView.setText(myJO.toString());
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
