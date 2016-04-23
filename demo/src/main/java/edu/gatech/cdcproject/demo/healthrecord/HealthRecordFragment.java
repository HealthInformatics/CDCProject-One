package edu.gatech.cdcproject.demo.healthrecord;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.clarifai.api.RecognitionResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import edu.gatech.cdcproject.demo.R;

/**
 * Created by guoweidong on 3/28/16.
 *
 * Edited by CW
 *
 */
public class HealthRecordFragment extends Fragment {
    private Button hRecord;
    private TextView textView;
    public static String serverBase = "http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base";
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_record, container, false);
        textView=(TextView)view.findViewById(R.id.return_record);
        hRecord = (Button) view.findViewById(R.id.get_record);
        textView.setText("hello");
        hRecord.setOnClickListener(
                new View.OnClickListener() {
            public void onClick(View v) {
                //FhirContext ctx = new FhirContext();
                //IGenericClient client = ctx.newRestfulGenericClient(serverBase);

                //Bundle bundle = client.search().forResource(Patient.class)
                //        .where(Patient.In)

                String dataURL="http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base/Patient/1142?_format=json";


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
                        textView.setText(result);
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














    private void setupUI() {


    }
}
