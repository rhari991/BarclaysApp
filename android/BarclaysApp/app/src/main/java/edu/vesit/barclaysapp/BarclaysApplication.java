package edu.vesit.barclaysapp;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.moxtra.sdk.MXAccountManager;
import com.moxtra.sdk.MXSDKException;

public class BarclaysApplication extends MultiDexApplication {

    private static final String TAG = "BarclaysApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MXAccountManager.createInstance(this, getString(R.string.moxtra_client_id), getString(R.string.moxtra_client_secret), true);
        } catch (MXSDKException.InvalidParameter invalidParameter) {
            Log.e(TAG, "Error when creating MXAccountManager instance.", invalidParameter);
        }
    }
}
