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
import com.example.adm.appservicios.getters_and_setters.MensajeChat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
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
    private String idServ;

    private ArrayList<MensajeChat> mensajes = new ArrayList<>();


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

        final Intent intent = getIntent();

        if(tipoUser.equals("Usuario")){
            idusu = settings.getString("UIDusuario","");

            /*Obtener id de trabajador*/
            idTrab = intent.getStringExtra("id");

            //Obtener id de servicios para regresar
            idServ = intent.getStringExtra("idServ");
        }else{
            idTrab = settings.getString("UIDusuario", "");
            idusu = intent.getStringExtra("ejemploID");
        }
        Log.d("chatActivity", "onCreate: TIPO = " + tipoUser);
        Log.d("chatActivity", "onCreate: UID = " + idusu);
        Log.d("chatActivity", "onCreate: TID = " + idTrab);


        initFirebase();

        FloatingActionButton sendButton = findViewById(R.id.chat_btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean emisor;
                if(tipoUser.equals("Usuario")){
                    emisor = true;
                }else{
                    emisor = false;
                }
                String message = ((EditText)findViewById(R.id.chat_edit)).getText().toString();
                final Map<String, Object> msg = new HashMap<>();
                msg.put("idusuario", idusu);
                msg.put("idservice", idTrab);
                msg.put("mensaje", message);
                String fecha = String.valueOf(System.currentTimeMillis());
                msg.put("fecha", fecha);
                msg.put("emisor",emisor);
                db.collection("chat").add(msg).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            //getAllMsg();
                            /*mensajes.add(new MensajeChat(idusu,
                                                            idTrab,
                                                            msg.get("fecha").toString(),
                                                            msg.get("mensaje").toString(),
                                                            emisor));
                            //recyclerViewAdapter.notifyDataSetChanged();
                            //recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerViewAdapter.notifyItemInserted(mensajes.size() - 1);*/

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
                if(tipoUser.equals("Usuario")){
                Intent intent = new Intent(ChatActivity.this, PostulacionesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", idServ); /*Obtener id del servicio*/
                startActivity(intent);
                finish();
                return true;}else{
                    Intent intent = new Intent(ChatActivity.this, MisServiciosActivity.class);
                    startActivity(intent);
                }

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

    private void getAllMsg() {

        recyclerViewAdapter = new ChatRecyclerViewAdapter(mensajes, tipoUser);
        recyclerView.setAdapter(recyclerViewAdapter);

        db.collection("chat").whereEqualTo("idusuario", idusu)
                .whereEqualTo("idservice", idTrab)
                .orderBy("fecha", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        // Handle errors
                        if (e != null) {
                            Log.w("chatActivity", "onEvent:error", e);
                            return;
                        }
                        // Dispatch the event
                        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                            // Snapshot of the changed document
                            DocumentSnapshot snapshot = change.getDocument();

                            switch (change.getType()) {
                                case ADDED:
                                    // TODO: handle document added
                                    onDocumentAdded(change);
                                    break;
                            }
                        }
                    }
                });

    }

    protected void onDocumentAdded(DocumentChange change) {
        DocumentSnapshot document = change.getDocument();
        MensajeChat mensaje = new MensajeChat(document.getString("idusuario"),
                document.getString("idservice"),
                document.getString("fecha"),
                document.getString("mensaje"),
                document.getBoolean("emisor"));
        mensajes.add(mensaje);
        recyclerViewAdapter.notifyItemInserted(mensajes.size() - 1);
        recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount());
        Log.d("chatActivity", "onDocumentAdded: Mensaje recibido: " + mensaje.getMensaje());
    }
}
