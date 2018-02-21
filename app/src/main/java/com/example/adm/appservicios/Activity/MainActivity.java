package com.example.adm.appservicios.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.adm.appservicios.Fragments.IndexFragment;
import com.example.adm.appservicios.Fragments.ServicesFragment;
import com.example.adm.appservicios.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*Declaracion de session */
        SharedPreferences settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

        Log.i("Session nombre: ", settings.getString("Nombreusuario",""));
        Log.i("Session Telefono: ", settings.getString("Telefonousuario",""));
        Log.i("Session UID: ", settings.getString("UIDusuario",""));
        Log.i("Session Tipo_user: ", settings.getString("Tipousuario",""));
        Log.i("Session Logueado: ", settings.getString("Logueadousuario",""));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentclass = null;

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Log.i("Click", "home");
            fragmentclass = IndexFragment.class;

        } else if (id == R.id.nav_services) {
            // Handle the camera action
            Log.i("Click", "services");
            fragmentclass = ServicesFragment.class;

        } else if (id == R.id.nav_pagos) {
            fragmentclass = ServicesFragment.class;

        } else if (id == R.id.nav_ubicacion) {
            fragmentclass = ServicesFragment.class;

        } else if (id == R.id.nav_builder) {
            fragmentclass = ServicesFragment.class;

        } else if (id == R.id.nav_info) {
            fragmentclass = ServicesFragment.class;

        }

        try{
            fragment = (Fragment) fragmentclass.newInstance();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.flcontent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
