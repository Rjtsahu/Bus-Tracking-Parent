package com.sahurjt.btsparent;

import android.app.Application;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.sahurjt.btsparent.toasty.Toasty;
import com.sahurjt.btsparent.utils.L;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @BindView(R.id.drawable_layout)
    public DrawerLayout drawerLayout;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    private TextView txtHeadName;
    private TextView txtHeadEmail;

    private SharedPrefHelper sharedPrefHelper;
    private static String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        TAG = this.getLocalClassName();
        sharedPrefHelper = SharedPrefHelper.getInstance(this);
        setToolbar();
        setDrawerLayout();
        setHomeFragment();
        setNavClickAction();
        setNavHeaderText();
    }

    private void setHomeFragment() {
        Fragment f = new HomeFragment();
        setFragment(f);
    }

    // short cut method
    private void setFragment(Fragment frag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentView, frag);
        fragmentTransaction.commit();

    }

    private void setDrawerLayout() {
        //drawerLayout= (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        actionBarDrawerToggle.syncState();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        try {
            ab.setHomeAsUpIndicator(R.drawable.ic_send);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.parent_home_appbar);
        } catch (NullPointerException e) {
            L.err(TAG, e.getMessage());
        }
    }

    private void setNavHeaderText() {
        txtHeadName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtHeaderName);
        txtHeadEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtHeaderEmail);
        txtHeadName.setText(sharedPrefHelper.getString(SharedPrefHelper.PARENT_NAME));
        txtHeadEmail.setText(sharedPrefHelper.getString(SharedPrefHelper.PARENT_EMAIL));
    }

    private void setNavClickAction() {
        if (navigationView == null) return;
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        drawerLayout.closeDrawers();
                        Fragment f;
                        int id = menuItem.getItemId();
                        switch (id) {
                            case android.R.id.home:
                                drawerLayout.openDrawer(GravityCompat.START);
                                return true;
                            case R.id.menuHome:
                                setHomeFragment();
                                return true;
                            case R.id.menuRecentRide:
                                f = new RecentRideFragment();
                                setFragment(f);
                                return true;
                            case R.id.menuActiveRide:
                                f = new ActiveRideFragment();
                                setFragment(f);
                                return true;
                            case R.id.menuFeedback:
                                f = new FeedbackFragment();
                                setFragment(f);
                                return true;
                            case R.id.menuSetting:
                                return true;
                            case R.id.menuAbout:
                                Toasty.success(getBaseContext(), "Developed by Rajat").show();
                                return true;
                        }
                        return true;
                    }
                });

    }

    // double press to exit
    private static long back_pressed;
    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        }
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            this.finishAffinity();
        } else {
            Toasty.info(getBaseContext(), "Press once again to exit!").show();
            back_pressed = System.currentTimeMillis();
        }
    }

    protected boolean isNavDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


}
