package com.clarifai.androidstarter;

import android.os.AsyncTask;

import org.json.JSONObject;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by Dingfeng on 3/9/2016.
 */
public class foodQuery
{
    private String initialAddress="http://api.nal.usda.gov/ndb/";
    private int ndbno;
    private String key="api_key="+Credentials.USDA_key;

    public JSONObject search_for_food(String food)
    {
        String url=initialAddress+"search/?format=json&q="+food+"&sort=n&max=1&offset=0&"+key;
        return create_json(url);
    }

    public JSONObject check_calories(String ndbno)
    {
        //http://api.nal.usda.gov/ndb/nutrients/?format=json&api_key=DEMO_KEY&nutrients=208&ndbno=01009
        String url=initialAddress+"nutrients/?format=json&"+key+"&nutrients=208&ndbno="+ndbno;
        return create_json(url);
    }


    public JSONObject create_json(String url)
    {
        JSONObject result=null;
        try {
            URL address = new URL(url);
            URLConnection urlConnection = address.openConnection();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            result=new JSONObject(responseStrBuilder.toString());
        }
        catch(Exception exception)
        {
            //result=exception.toString();
        }
        if(result.has("list")||result.has("report"))
            return result;
        else
            return null;
    }






}
