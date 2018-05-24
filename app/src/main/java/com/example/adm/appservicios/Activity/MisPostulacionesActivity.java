package com.example.adm.appservicios.Activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.adm.appservicios.Adapters.MisPostRecyclerViewAdapter;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Servicios_worker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MisPostulacionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager lManager;

    /*Variables para base de datos*/
    FirebaseFirestore db;

    /*Variable para session*/
    SharedPreferences settings;

    /*Arreglo de postulaciones obtenidas*/
    private ArrayList<Servicios_worker> postulaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_postulaciones);

        /*Toolbar*/
        Toolbar toolbar = findViewById(R.id.misPost_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis Postulaciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d("misPostulaciones", "onCreate: Toolbar creada");

        /*RecyclerView*/
        recyclerView = findViewById(R.id.misPosts_recyclerView);
        recyclerView.setHasFixedSize(true);

        lManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lManager);

        settings = this.getSharedPreferences("sesion_user", MODE_PRIVATE);
        Log.d("misPostulaciones", "onCreate: " + settings.getString("UIDusuario",""));

        initFirebase();
    }

    private void initFirebase(){
        db = FirebaseFirestore.getInstance();
        getMisPostulaciones();
    }

    private void getMisPostulaciones(){
        adapter = new MisPostRecyclerViewAdapter(postulaciones);
        recyclerView.setAdapter(adapter);
        Log.d("misPostulaciones", "getMisPostulaciones: idusu_trab = " + settings.getString("UIDusuario",""));
        db.collection("postulaciones_service").whereEqualTo("idusu_trab", settings.getString("UIDusuario",""))
                                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                        for(final DocumentSnapshot document: documentSnapshots){
                                                            Log.d("misPostulaciones", "onEvent: POSTULACIONES OBTENIDAS!!!");
                                                            db.collection("services").document(document.getString("service")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                                                        postulaciones.add(new Servicios_worker(documentSnapshot.getString("idusuario"),/*ID del usuario con quien chatear*/
                                                                                                                documentSnapshot.getString("Servicio"),
                                                                                                                documentSnapshot.getString("Descripcion"),
                                                                                                                documentSnapshot.getString("Direccion"),
                                                                                                                documentSnapshot.getString("Min")));

                                                                        adapter.notifyDataSetChanged();
                                                                    Log.d("misPostulaciones", "onEvent2: Postulacion recibida");
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
