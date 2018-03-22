package com.example.adm.appservicios.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.adm.appservicios.Adapters.Adapter_services_required;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Servicios_worker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class PostularseActivity extends AppCompatActivity {

    /*Firebase*/
    FirebaseFirestore db;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private MaterialProgressBar materialProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postularse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Postularse");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Declaracion de session */
        settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

        recycler = (RecyclerView) findViewById(R.id.idservice);
        recycler.setHasFixedSize(true);

        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        materialProgressBar = (MaterialProgressBar) findViewById(R.id.progress_bar);

        // Inicializacion de Firebase database
        initFirebase();
    }

    /*Inicializacion de Firebase*/
    private void initFirebase() {
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        getPosiblePost();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                /*Intent to Main, pass parameter id = 2*/
                Intent intent = new Intent(PostularseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", "2");
                startActivity(intent);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getPosiblePost()
    {
        final List services = new ArrayList();
        adapter = new Adapter_services_required(services);

        /*Obtener servicios de Trabajador*/
        ListenerRegistration listenerRegistration = db.collection("services")
                .whereEqualTo("Estatus", "Pendiente")
                .whereEqualTo("Servicio", settings.getString("Tipousuario",""))
                .whereEqualTo("idatiende", "")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable final QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        /*Validar si hay registro*/
                        if (value.size() > 0) {

                            /*Recorrido de datos*/
                            for (DocumentSnapshot doc : value) {
                                Log.i("Datos reco Descripcion " , doc.getString("Descripcion"));

                                /*Se agrega datos de servicio a arraylist*/
                                services.add(new Servicios_worker(doc.getId(), doc.getString("Descripcion"), doc.getString("Servicio"), doc.getString("Direccion"), doc.getString("Min")));
                                adapter.notifyDataSetChanged();
                            }

                            recycler.setAdapter(adapter);

                            /*Se oculta ProgressBar*/
                            materialProgressBar.setIndeterminate(false);
                            materialProgressBar.setVisibility(View.GONE);

                        }
                        else
                        {
                            /*Se oculta ProgressBar*/
                            materialProgressBar.setIndeterminate(false);
                            materialProgressBar.setVisibility(View.GONE);

                            /*Mostrar mensaje de exito*/
                            Toast.makeText (PostularseActivity.this, "No existen servicios.", Toast.LENGTH_SHORT).show ();
                        }

                    }
                });

    }

}
