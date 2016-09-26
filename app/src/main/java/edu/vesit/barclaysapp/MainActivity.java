package edu.vesit.barclaysapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements CustomInterface{

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;

    private Context mContext;
    private ActionBarDrawerToggle drawerToggle;
    public final static int DRAW_OVERLAYS_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = MainActivity.this;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.Add, R.string.Add){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mContext.startService(new Intent(mContext, BarclaysAppService.class));
        checkDrawOverlayPermission();
        selectDrawerItem(navigationView.getMenu().getItem(0));
    }

    @TargetApi(23)
    public void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(mContext)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVERLAYS_REQUEST_CODE);
        }
    }

    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == DRAW_OVERLAYS_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(mContext, "Sorry, this application requires this permission.", Toast.LENGTH_SHORT).show();
                logout();
            }
        }
    }

    @Override
    public void onDestroy() {
        mContext.stopService(new Intent(mContext, BarclaysAppService.class));
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public static String getStringResource(int resid) {
        return getStringResource(resid);
    }

    @Override
    public void exchangeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public void selectDrawerItem(MenuItem menuItem) {

        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_twitter:
                fragment = new TwitterFragment();
                break;
            case R.id.nav_chat:
                fragment = new ChatFragment(this);
                break;
            default:
                fragment = new PlaceholderFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        setTitle(menuItem.getTitle());
        if(menuItem.isChecked())
            menuItem.setChecked(false);
        else
            menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
