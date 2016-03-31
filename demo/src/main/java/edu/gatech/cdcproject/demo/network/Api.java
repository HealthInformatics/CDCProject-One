package edu.gatech.cdcproject.demo.network;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import edu.gatech.cdcproject.backend.myApi.MyApi;
import edu.gatech.cdcproject.demo.BuildConfig;

/**
 * Created by mkatri on 11/22/15.
 */
public class Api {
    private static MyApi myApi = null;

    static {
        MyApi.Builder builder = new MyApi.Builder
                (AndroidHttp.newCompatibleTransport(), new
                        AndroidJsonFactory(), null)
                .setRootUrl(BuildConfig.APP_HOME + "/_ah/api/");
        builder.setApplicationName("CDC Project");
        myApi = builder.build();
    }

    public static MyApi getClient() {
        return myApi;
    }
}
