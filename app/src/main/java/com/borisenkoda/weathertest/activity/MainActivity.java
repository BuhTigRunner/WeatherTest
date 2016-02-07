package com.borisenkoda.weathertest.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.app.App;
import com.borisenkoda.weathertest.databinding.ActivityMainBinding;
import com.borisenkoda.weathertest.fragments.CityListFragment;
import com.borisenkoda.weathertest.helpers.FragmentStack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentStack fragmentStack;
    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        App.inject(this);
        fragmentStack = new FragmentStack(getSupportFragmentManager(), R.id.fragments);
        toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null){
            fragmentStack.replace(new CityListFragment());

        }
        setSupportActionBar(binding.appBar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        fragmentStack.setOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateHamburgerButton();
            }
        });
        updateHamburgerButton();

    }

    private void updateHamburgerButton() {
        if (getSupportActionBar() == null || toggle == null) return;
        boolean isHaveBackFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        toggle.setDrawerIndicatorEnabled(!isHaveBackFragments);
        invalidateOptionsMenu();
    }


    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            fragmentStack.peek();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_go_www || id == R.id.nav_source_github)  {
            String url = getString(id == R.id.nav_go_www ? R.string.openweathermap_site : R.string.github_site);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (toggle == null) return;
        toggle.onConfigurationChanged(newConfig);
    }

    public FragmentStack getFragmentStack() {
        return fragmentStack;
    }
}
