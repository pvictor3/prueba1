package com.example.adm.appservicios.Activity;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.adm.appservicios.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MisServiciosActivity extends AppCompatActivity {

    /*Firebase*/
    FirebaseFirestore db;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_servicios);

        Log.i("Vista - ", "Mis Servicios");

        /*Declaracion de session */
        settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

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
        ListenerRegistration listenerRegistration = db.collection("address")
                .whereEqualTo("User", settings.getString("UIDusuario",""))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        /*Validar si hay registro*/
                        if (value.size() > 0) {

                                    /*Declaracion inicial de arrayadpater*/
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MisServiciosActivity.this, android.R.layout.select_dialog_singlechoice);

                                    /*Recorrido de datos*/
                            for (DocumentSnapshot doc : value) {

                                Log.i("Direccion", doc.getString("Direccion"));

                                arrayAdapter.add(doc.getString("Direccion"));

                            }

                        } else {
                            Log.i("Error", "No existe Direcciones para el usuario");

                        }

                    }
                });
    }

}
