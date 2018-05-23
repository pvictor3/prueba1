package com.example.adm.appservicios.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.example.adm.appservicios.Adapters.adapter_postulaciones;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Servicios_worker;
import com.example.adm.appservicios.getters_and_setters.workers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostulacionesActivity extends AppCompatActivity {

    private String id;
    private RecyclerView.Adapter adapter;
    private RecyclerView recycler;
    private RecyclerView.LayoutManager lManager;

    /*Firebase*/
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postulaciones);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Postulados");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler = (RecyclerView) findViewById(R.id.postulados_recycler);
        recycler.setHasFixedSize(true);

        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        /*Se recibe id de servicio seleccionado*/
        final Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.i("Id recibido", id);

        // Inicializacion de Firebase database
        initFirebase();

    }

    /*Inicializacion de Firebase*/
    private void initFirebase() {
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        getPostulaciones(id);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(PostulacionesActivity.this, MisServiciosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getPostulaciones(String id)
    {
        final List services = new ArrayList();
        adapter = new adapter_postulaciones(services, id); /*Enviar id al adapter para pasarselo a ChatActivity y pueda regresar*/

        /*Obtener servicios de Trabajador*/
        ListenerRegistration listenerRegistration = db.collection("postulaciones_service")
                .whereEqualTo("service", id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable final QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        /*Validar si hay registro*/
                        if (value.size() > 0) {

                            /*Recorrido de datos*/
                            for (DocumentSnapshot doc : value) {
                                Log.i("Datos reco idusu_trab " , doc.getString("idusu_trab"));

                                /*Obtener servicios de Trabajador*/
                                DocumentReference docRef = db.collection("users").document(doc.getString("idusu_trab"));
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();

                                            Log.i("Datos Nombre " , document.getString("Nombre"));

                                            /*Se agrega datos de servicio a arraylist*/
                                            services.add(new workers(document.getId(), document.getString("Nombre"), document.getString("Tipo_user")));
                                            adapter.notifyDataSetChanged();

                                        } else {
                                            Log.d("Failed ", "get failed with ", task.getException());
                                        }
                                    }
                                });

                            }

                            recycler.setAdapter(adapter);


                        }
                        else
                        {
                            /*Mostrar mensaje de exito*/
                            Toast.makeText (PostulacionesActivity.this, "No existen postulaciones.", Toast.LENGTH_SHORT).show ();
                        }

                    }
                });
    }

}
