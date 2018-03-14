package com.example.adm.appservicios.Activity;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

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

public class MisServiciosActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_mis_servicios);

        /*Declaracion de session */
        settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

        recycler = (RecyclerView) findViewById(R.id.servicesAsked_RecyclerView);
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

        getServices();

    }

    public void getServices(){

        final List services = new ArrayList();
        adapter = new Adapter_services_required(services);

        /*Obtener ubicaciones existentes de base de datos*/
        ListenerRegistration listenerRegistration = db.collection("services")
                .whereEqualTo("idusuario", settings.getString("UIDusuario",""))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        /*Validar si hay registro*/
                        if (value.size() > 0) {

                            /*Recorrido de datos*/
                            for (DocumentSnapshot doc : value) {

                                /*Se agrega datos de servicio a arraylist*/
                                services.add(new Servicios_worker(doc.getId(), doc.getString("Descripcion"), doc.getString("Servicio"), doc.getString("Direccion"), doc.getString("Min")));
                                adapter.notifyDataSetChanged();
                            }

                            recycler.setAdapter(adapter);

                            /*Se oculta ProgressBar*/
                            materialProgressBar.setIndeterminate(false);
                            materialProgressBar.setVisibility(View.GONE);

                        } else {
                            /*No existen servicios registrados por usuario*/
                            Log.i("Error", "No existe Direcciones para el usuario");

                        }

                    }
                });
    }

}
