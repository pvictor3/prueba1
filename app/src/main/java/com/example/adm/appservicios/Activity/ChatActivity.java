package com.example.adm.appservicios.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adm.appservicios.Adapters.ChatRecyclerViewAdapter;
import com.example.adm.appservicios.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    SharedPreferences settings;

    FirebaseFirestore db;
    Query chatQuery;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager lManager;
    private RecyclerView.Adapter recyclerViewAdapter;

    private String idusu;
    private String idTrab;
    private String tipoUser;

    private ArrayList<String> mensajes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mensajes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Obtener recycler
        recyclerView = findViewById(R.id.chat_recycler);
        //recyclerView.setHasFixedSize(true);

        //Conectarlo a un linearlayout
        lManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lManager);

        /*Recibir idusu_trab*/
        settings = this.getSharedPreferences("sesion_user", MODE_PRIVATE);
        tipoUser = settings.getString("Tipousuario", "");
        idusu = settings.getString("UIDusuario","");
        Log.d("chatActivity", "onCreate: UID = " + idusu);

        /*Obtener id de trabajador*/
        final Intent intent = getIntent();
        idTrab = intent.getStringExtra("id");
        Log.d("chatActivity", "onCreate: TID = " + idTrab);


        initFirebase();

        FloatingActionButton sendButton = findViewById(R.id.chat_btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = ((EditText)findViewById(R.id.chat_edit)).getText().toString();
                final Map<String, Object> msg = new HashMap<>();
                msg.put("idusuario", idusu);
                msg.put("idservice", idTrab);
                msg.put("mensaje", message);
                String fecha = String.valueOf(System.currentTimeMillis());
                msg.put("fecha", fecha);
                db.collection("chat").add(msg).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            //getAllMsg();
                            mensajes.add(msg.get("mensaje").toString());
                            recyclerViewAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount());
                            Log.d("chatActivity", "onComplete: Mensaje enviado " + msg.get("mensaje"));
                            ((EditText)findViewById(R.id.chat_edit)).setText("");
                        }
                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(ChatActivity.this, PostulacionesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", "Hz17RmuOKHfD1dBbFHPE"); /*Obtener id del servicio*/
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initFirebase(){
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();
        chatQuery = db.collection("chat").whereEqualTo("idusuario", idusu)
                .whereEqualTo("idservice", idTrab).orderBy("fecha", Query.Direction.ASCENDING).limit(50);
        Log.d("chat","initFirebase");
        getAllMsg();
    }

    private void getAllMsg(){

        recyclerViewAdapter = new ChatRecyclerViewAdapter(mensajes);
        chatQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document : task.getResult()){
                    String mensaje = document.getString("mensaje");
                    mensajes.add(mensaje);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount());
            }
        });
    }
}
