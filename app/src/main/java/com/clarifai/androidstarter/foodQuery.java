package com.clarifai.androidstarter;

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
public class foodQuery {
    private String initialAddress="http://api.nal.usda.gov/ndb/";
    private int ndbno;
    private String key="api_key="+Credentials.USDA_key;

    public String search_for_food(String food)
    {
        //String url=initialAddress+"search/?format=json&q="+food+"&sort=n&max=1&offset=0&"+key;
        String url="http://api.nal.usda.gov/ndb/search/?format=json&q=apple&api_key=vF66hdnI9EUf8DsBjHIVpmCoFiUIfXUIWKFjTunA";
        return create_json(url);
    }

    public String create_json(String url)
    {
        String result="12";
        try {
            URL address = new URL(url);
            URLConnection urlConnection = address.openConnection();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            result=responseStrBuilder.toString();
        }
        catch(Exception exception)
        {
            result=exception.toString();

        }
        return result;
    }






}
