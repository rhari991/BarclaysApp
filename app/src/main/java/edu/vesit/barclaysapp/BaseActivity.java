package edu.vesit.barclaysapp;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moxtra.binder.ui.base.MXActivity;
import com.moxtra.sdk.MXAccountManager;
import com.moxtra.sdk.MXSDKConfig;

public class BaseActivity extends MXActivity implements MXAccountManager.MXAccountUnlinkListener {

    private static final String TAG = "BaseActivity";
    protected MenuItem miActionProgressItem;
    protected boolean isLoading;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        if (isLoading)
            miActionProgressItem.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void showProgressBar(boolean ifShow) {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(ifShow);
        }
    }

    protected void logout() {
        PreferenceUtil.removeUser(this);
        miActionProgressItem.setVisible(true);
        boolean ret = MXAccountManager.getInstance().unlinkAccount(this);
        if (!ret) {
            miActionProgressItem.setVisible(false);
            Log.e(TAG, "Can't logout: the unlinkAccount return false.");
            Toast.makeText(this, "unlink failed.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUnlinkAccountDone(MXSDKConfig.MXUserInfo mxUserInfo) {
        miActionProgressItem.setVisible(false);
        if (mxUserInfo == null) {
            Log.e(TAG, "Can't logout: the mxUserInfo is null.");
            Toast.makeText(this, "unlink failed as mxUserInfo is null.", Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "User " + mxUserInfo.userIdentity + " logout OK.");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
