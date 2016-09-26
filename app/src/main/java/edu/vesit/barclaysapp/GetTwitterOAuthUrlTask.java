package edu.vesit.barclaysapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GetTwitterOAuthUrlTask extends AsyncTask<Void, Void, String> {

    private Context mContext;

    public GetTwitterOAuthUrlTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(Void... params) {

        final OAuth10aService service = new ServiceBuilder()
                .apiKey(mContext.getResources().getString(R.string.twitter_api_key))
                .apiSecret(mContext.getResources().getString(R.string.twitter_api_secret))
                .build(TwitterApi.instance());
        OAuth1RequestToken requestToken = null;
        try {
            requestToken = service.getRequestToken();
        }
        catch (IOException e) {
            Log.e("Request token error ", e.getMessage());
        }
        return service.getAuthorizationUrl(requestToken);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("login url ", "" + result);
        EventBus.getDefault().post(new ServerResponseEvent(result));
        super.onPostExecute(result);
    }
}
