package edu.vesit.barclaysapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

public class PreferenceUtil {

    private final static String PREF = "BARCLAYSAPP_PREF";
    private final static String UID = "UID";
    private final static String EMAIL = "EMAIL";
    private final static String PASSWORD = "PASSWORD";
    private final static String FIRST_NAME = "FIRST_NAME";
    private final static String LAST_NAME = "LAST_NAME";
    private final static String AVATAR_PATH = "AVATAR_PATH";
    private final static String TWITTER_TOKEN = "TWITTER_TOKEN";
    private static final String TAG = "PreferenceUtil";

    private static String getClientId(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Can't get metadata.", e);
        }
        if (appInfo != null && appInfo.metaData != null) {
            String clientId = appInfo.metaData.getString("com.moxtra.sdk.ClientId");
            return clientId;
        }
        return null;
    }

    public static void saveUser(Context context, User user) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(UID, user.getUid());
        ed.putString(EMAIL, user.getEmail());
        ed.putString(PASSWORD, user.getPassword());
        ed.putString(FIRST_NAME, user.getFirstName());
        ed.putString(LAST_NAME, user.getLastName());
        ed.putString(AVATAR_PATH, user.getAvatarPath());
        ed.putString(TWITTER_TOKEN, user.getTwitterToken());
        ed.commit();
    }

    public static void removeUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.clear();
        ed.commit();
    }

    public static User getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String uid = sp.getString(UID, null);
        if (uid == null)
            return null;
        User user = new User();
        user.setUid(uid);
        user.setEmail(sp.getString(EMAIL, null));
        user.setPassword(sp.getString(PASSWORD, null));
        user.setFirstName(sp.getString(FIRST_NAME, null));
        user.setLastName(sp.getString(LAST_NAME, null));
        user.setAvatarPath(sp.getString(AVATAR_PATH, null));
        user.setTwitterToken(sp.getString(TWITTER_TOKEN, null));
        return user;
    }

    public static boolean isUserInit(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String clientId = getClientId(context);
        boolean isInit = sp.getBoolean(clientId, false);
        return isInit;
    }

    public static void setUserInitDone(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(getClientId(context), true).commit();
    }
}

