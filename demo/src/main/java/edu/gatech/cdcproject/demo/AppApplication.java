package edu.gatech.cdcproject.demo;

import android.app.Application;
import android.content.Intent;

import com.google.android.gms.security.ProviderInstaller;

import static edu.gatech.cdcproject.demo.util.LogUtils.LOGE;
import static edu.gatech.cdcproject.demo.util.LogUtils.LOGW;
import static edu.gatech.cdcproject.demo.util.LogUtils.makeLogTag;

/**
 * Created by guoweidong on 3/28/16.
 */
public class AppApplication extends Application {
    private static final String TAG = makeLogTag(AppApplication.class);
    @Override
    public void onCreate() {
        super.onCreate();

        // Ensure an updated security provider is installed into the system when a new one is
        // available via Google Play services.
        try {
            ProviderInstaller.installIfNeededAsync(getApplicationContext(),
                    new ProviderInstaller.ProviderInstallListener() {
                        @Override
                        public void onProviderInstalled() {
                            LOGW(TAG, "New security provider installed.");
                        }

                        @Override
                        public void onProviderInstallFailed(int errorCode, Intent intent) {
                            LOGE(TAG, "New security provider install failed.");
                            // No notification shown there is no user intervention needed.
                        }
                    });
        } catch (Exception ignorable) {
            LOGE(TAG, "Unknown issue trying to install a new security provider.", ignorable);
        }
    }
}
