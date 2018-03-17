package com.example.adm.appservicios.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.adm.appservicios.Database.SQLiteHandler;
import com.example.adm.appservicios.Fragments.IndexFragment;
import com.example.adm.appservicios.Fragments.InfoFragment;
import com.example.adm.appservicios.Fragments.MenuServicesFragment;
import com.example.adm.appservicios.Fragments.PagosFragment;
import com.example.adm.appservicios.Fragments.ProfileFragment;
import com.example.adm.appservicios.Fragments.RegisterOficioFragment;
import com.example.adm.appservicios.Fragments.ServicesFragment;
import com.example.adm.appservicios.Fragments.UbicacionesFragment;
import com.example.adm.appservicios.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment = null;
    Class fragmentclass = null;
    FragmentManager fragmentManager = getSupportFragmentManager();
    public static GoogleMap mMap;

    TextView TituloNombre, TituloPerfil, TituloTelefono;
    ImageView ImagePerfil;

    private SQLiteHandler datab;

    SharedPreferences settings;

    /*Firebase*/
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Llamado a funcion para inicializar firebase*/
        initFirebase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*Declaracion de session */
        settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

        Log.i("Session nombre ", settings.getString("Nombreusuario",""));
        Log.i("Session Telefono ", settings.getString("Telefonousuario",""));
        Log.i("Session UID ", settings.getString("UIDusuario",""));
        Log.i("Session Tipo_user ", settings.getString("Tipousuario",""));
        Log.i("Session Logueado ", settings.getString("Logueadousuario",""));

        /*Obtener vista nav_header para asignar texto a los titulos*/
        View header     = navigationView.getHeaderView(0);
        TituloNombre    = header.findViewById(R.id.textViewNameOnNavigation);
        TituloPerfil    = header.findViewById(R.id.tipo_de_perfil);
        TituloTelefono  = header.findViewById(R.id.textViewPhoneOnNavigation);
        ImagePerfil     = header.findViewById(R.id.navViewImageView_ProfilePicture);

        /*Validar si existe usuario*/
        db.collection("users")
                .whereEqualTo("Telefono", settings.getString("Telefonousuario",""))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                        /*Validar si hay registro*/
                        if (documentSnapshots.size() > 0) {
                            /*Recorrido de datos*/
                            for (DocumentSnapshot doc : documentSnapshots)
                            {
                                /*Asignacion de textos a titulos*/
                                TituloNombre.setText(doc.getString("Nombre"));
                                TituloTelefono.setText(doc.getString("Telefono"));
                                TituloPerfil.setText(doc.getString("Tipo_user"));

                                SharedPreferences.Editor editor;
                                editor = settings.edit();
                                editor.putString("Nombreusuario" , doc.getString("Nombre"));
                                editor.putString("Telefonousuario" , doc.getString("Telefono"));
                                editor.putString("Tipousuario" , doc.getString("Tipo_user"));

                                if (doc.getString("Image_user") != "")
                                {
                                    // Reference to an image file in Firebase Storage
                                    StorageReference storageReference = storage.getReferenceFromUrl(doc.getString("Image_user"));

                                    Glide.with(MainActivity.this)
                                            .using(new FirebaseImageLoader())
                                            .load(storageReference)
                                            .into(ImagePerfil);
                                }

                            }

                        }

                    }
                });

        /*Click en imagen de perfil de usuario*/
        ImagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectAccount();
            }
        });

        /*Inicializacion de SQLite*/
        datab = new SQLiteHandler(getBaseContext());

        /*Inicializacion de fragment default index*/
        fragmentclass = IndexFragment.class;

        try{
            fragment = (Fragment) fragmentclass.newInstance();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.flcontent, fragment).commit();

    }

    /*Inicializacion de Firebase*/
    private void initFirebase() {
        FirebaseApp.initializeApp(this);

        db                  = FirebaseFirestore.getInstance();
        storage             = FirebaseStorage.getInstance();
        storageReference    = storage.getReference();

    }

    public void redirectAccount(){
        Log.i("Click", "Image");
        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            logoutUser();
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Index
            fragmentclass = IndexFragment.class;

        } else if (id == R.id.nav_services) {
            // Servicios
            fragmentclass = MenuServicesFragment.class;

        } else if (id == R.id.nav_pagos) {
            // Pagos
            fragmentclass = PagosFragment.class;

        } else if (id == R.id.nav_ubicacion) {
            //Ubicaciones
            fragmentclass = UbicacionesFragment.class;

        } else if (id == R.id.nav_builder) {

            fragmentclass = RegisterOficioFragment.class;

        } else if (id == R.id.nav_info) {
            // Acerca de
            fragmentclass = InfoFragment.class;

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

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        Log.i("Info->", "OnMapReady");
//        mMap = googleMap;
//        mMap.getUiSettings().setMapToolbarEnabled(false);
//        mMap.getUiSettings().setZoomControlsEnabled(false);
//        setCurretPosition();
//    }

//    public void setCurretPosition(){
//        LatLng marker = new LatLng(19.33978502, -99.19086277 );
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
//    }

    private void logoutUser() {
        /*Declaracion de session */
        SharedPreferences settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString("Nombreusuario" , "");
        editor.putString("Telefonousuario" , "");
        editor.putString("UIDusuario" , "");
        editor.putString("Tipousuario" , "");
        editor.putString("Logueadousuario" , "false");

        editor.apply();

        datab.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
        startActivity(intent);
        finish();
    }
}
