package edu.vesit.barclaysapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PythonServerUtils {

    public static void sendMessageToServer(Context context, String message) {
        message = message.toLowerCase();
        //if(message.contains("send message"))
            //BarclaysAppService.sendChatMessage(message);
        //else {
            User user = PreferenceUtil.getUser(context);
            PythonServerTask tcpSend = new PythonServerTask(encodeText(user.getUid(), message));
            tcpSend.execute();
        //}
    }

    public static void requestTweet(Context context, TweetEvent tweetEvent) {
        User user = PreferenceUtil.getUser(context);
        PythonServerTask tcpSend = new PythonServerTask(encodeTweetRequest(user.getUid(), tweetEvent));
        tcpSend.execute();
    }

    public static void sendParameterToServer(Context context, String parameter, String value) {
        User user = PreferenceUtil.getUser(context);
        PythonServerTask tcpSend= new PythonServerTask(encodeText(user.getUid(), parameter, value));
        tcpSend.execute();
    }

    public static String encodeTweetRequest(String uid, TweetEvent tweetEvent) {
        try {
            JSONObject response = new JSONObject();
            response.put("uid", uid);
            response.put("parameter", "kk my tweet");
            response.put("verifier", tweetEvent.getOauthVerifier());
            response.put("tweet", tweetEvent.getMessage());
            return response.toString();
        }
        catch (JSONException e) {
            Log.e("Parsing error ", e.getMessage());
            return "";
        }
    }

    public static String encodeText(String uid, String message) {
        try {
            JSONObject response = new JSONObject();
            response.put("uid", uid);
            response.put("parameter", message);
            //response.put("parameter", "message");
            //response.put("message", message);
            return response.toString();
        }
        catch (JSONException e) {
            Log.e("Parsing error ", e.getMessage());
            return "";
        }
    }

    public static String encodeText(String uid, String parameter, String value) {
        try {
            JSONObject response = new JSONObject();
            response.put("uid", uid);
            response.put("parameter", parameter + " " + value);
            //response.put("parameter", parameter);
            //response.put(parameter, value);
            return response.toString();
        }
        catch (JSONException e) {
            Log.e("Parsing error ", e.getMessage());
            return "";
        }
    }
}
