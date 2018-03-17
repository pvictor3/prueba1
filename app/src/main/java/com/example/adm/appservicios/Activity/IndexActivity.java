package com.example.adm.appservicios.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adm.appservicios.Database.SQLiteHandler;
import com.example.adm.appservicios.Helpers.User;
import com.example.adm.appservicios.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class IndexActivity extends AppCompatActivity {

    /*Declaracion de variables para inicializacion de firebase*/
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    FirebaseFirestore db;

    private SQLiteHandler datab;

    Button btnSignUp, btnRegister;

    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // Inicializacion de vista
        btnSignUp       = (Button)findViewById(R.id.btnSignIn);
        btnRegister     = (Button)findViewById(R.id.btnRegister);
        rootLayout      = (RelativeLayout)findViewById(R.id.rootLayout);

        // Llamada a funciones
        initFirebase();

        /*Llamada a vistas*/
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterdialog();

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        /*Declaracion de session */
        SharedPreferences settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

        /*Log.i("Session nombre: ", settings.getString("Nombreusuario",""));
        Log.i("Session Telefono: ", settings.getString("Telefonousuario",""));
        Log.i("Session UID: ", settings.getString("UIDusuario",""));
        Log.i("Session Tipo_user: ", settings.getString("Tipousuario",""));
        Log.i("Session Logueado: ", settings.getString("Logueadousuario",""));*/

        if (Boolean.parseBoolean(settings.getString("Logueadousuario","")))
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    /*Inicializacion de Firebase*/
    private void initFirebase() {
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        //SQLite database handler
        datab = new SQLiteHandler(getBaseContext());

    }

    public void onSuccess(QuerySnapshot documentSnapshots) {
        Log.i("Success", "data");
    }

    /*Registrar*/
    private void showRegisterdialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        /*Titulo de alert*/
        dialog.setTitle("Registrar");
        /*Mensaje de alert*/
        dialog.setMessage("Por favor use su Número de Telefono para registrarse.");

        /*Crear layout register*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout    = inflater.inflate(R.layout.activity_register, null);

        final MaterialEditText edtName      = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPassword  = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtPhone     = register_layout.findViewById(R.id.edtPhone);

        /*Asignar vista a alert*/
        dialog.setView(register_layout);

        /*Asignación de textos a botones de alert*/
        dialog.setPositiveButton("Registrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // Disable button in if processing
                btnSignUp.setEnabled(false);

                if (TextUtils.isEmpty(edtName.getText().toString()))
                {
                    Snackbar.make(rootLayout, "Por favor introduce tu Nombre y Apellidos", Snackbar.LENGTH_SHORT)
                            .show();

                    return;
                }

                if (TextUtils.isEmpty(edtPassword.getText().toString()))
                {
                    Snackbar.make(rootLayout, "Please enter Password", Snackbar.LENGTH_SHORT)
                            .show();

                    return;
                }

                if (edtPassword.getText().toString().length() < 4)
                {
                    Snackbar.make(rootLayout, "Contraseña demasiado corta", Snackbar.LENGTH_SHORT)
                            .show();

                    return;
                }

                if (TextUtils.isEmpty(edtPhone.getText().toString()))
                {
                    Snackbar.make(rootLayout, "Por favor introduce tu Número de Telefono", Snackbar.LENGTH_SHORT)
                            .show();

                    return;
                }

                final SpotsDialog waitingDialog = new SpotsDialog(IndexActivity.this);
                waitingDialog.show();

//                DatabaseReference mDatabase;
////// ...
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//
//                User user = new User(edtName.getText().toString(), edtPhone.getText().toString(), edtPassword.getText().toString(), "Usuario");
//
//                mDatabase.child("users/"+ UUID.randomUUID().toString()).setValue(user);

                /*Validar si existe usuario*/
                db.collection("users")
                        .whereEqualTo("Telefono", edtPhone.getText().toString())
                        .whereEqualTo("Contrasena", edtPassword.getText().toString())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                //Log.i("sucess", String.valueOf(value.size()));

                                /*Validar si hay registro*/
                                if (value.size() > 0) {
                                    waitingDialog.dismiss();
                                    /*Mostrar mensaje de exito*/
                                    Toast.makeText (IndexActivity.this, "Ya existe un usuario registrado, intente con otro.", Toast.LENGTH_SHORT).show ();

                                } else {
                                    Log.i("Error", "No existe usuario registrado");

                                    Log.i("Nuevo usuario", "creando...");
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Nombre", edtName.getText().toString());
                                    user.put("Telefono", edtPhone.getText().toString());
                                    user.put("Contrasena", edtPassword.getText().toString());
                                    user.put("Tipo_user", "Usuario");
                                    user.put("Image_user", "");
                                    user.put("Lat", "");
                                    user.put("Lng", "");

                                    /*Registro de nuevo usuario*/
                                    db.collection("users")
                                            .add(user)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.i("Informacion", "Documento anadido a base de datos" + documentReference.getId());
                                                    waitingDialog.dismiss();

                                                    /*Mostrar mensaje de exito*/
                                                    Toast.makeText (IndexActivity.this, "Usuario creado con éxito.", Toast.LENGTH_SHORT).show ();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i("informacion", "Error adding document", e);
                                                    waitingDialog.dismiss();
                                                    /*Mostrar mensaje de error*/
                                                    Toast.makeText (IndexActivity.this, "No se pudo crear el usuario, intente de nuevo.", Toast.LENGTH_SHORT).show ();
                                                }
                                            });
                                }

                            }
                        });

            }
        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edtName.setText("");
                edtPhone.setText("");
                edtPassword.setText("");
                dialog.dismiss();
            }
        });

        /*Mostrar alert*/
        dialog.show();

    }

    /*Entrar*/
    private void showLoginDialog() {
        /*Crear alert*/
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        /*Titulo de alert*/
        dialog.setTitle("Entrar");
        /*Mensaje de alert*/
        dialog.setMessage("Ingrese con su Número de Telefono y contraseña");

        /*Crear layout login*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.activity_login, null);

        final MaterialEditText edtNumero = login_layout.findViewById(R.id.edtNumero);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPassword);

        /*Asignar layout login a alert*/
        dialog.setView(login_layout);

        /*Asignación de textos a botones de alert*/
        dialog.setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // Disable button in if processing
                btnSignUp.setEnabled(false);

                if (TextUtils.isEmpty(edtNumero.getText().toString()))
                {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                            .show();

                    return;
                }

                if (TextUtils.isEmpty(edtPassword.getText().toString()))
                {
                    Snackbar.make(rootLayout, "Please enter Password", Snackbar.LENGTH_SHORT)
                            .show();

                    return;
                }

                final SpotsDialog waitingDialog = new SpotsDialog(IndexActivity.this);
                waitingDialog.show();

                Log.i("Telefono", edtNumero.getText().toString());
                Log.i("Contraseña", edtPassword.getText().toString());

                ListenerRegistration listenerRegistration = db.collection("users")
                        .whereEqualTo("Telefono", edtNumero.getText().toString())
                        .whereEqualTo("Contrasena", edtPassword.getText().toString())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                //Log.i("sucess", String.valueOf(value.size()));

                                /*Validar si hay registro*/
                                if (value.size() > 0) {
                                    /*Recorrido de datos*/
                                    for (DocumentSnapshot doc : value) {
                                        //Obtener id de usuario registrado -> doc.getId()
                                        /*
                                        Log.i("Nombre", doc.getString("Nombre"));
                                        Log.i("Telefono", doc.getString("Telefono"));
                                        Log.i("Contrasena", doc.getString("Contrasena"));*/

                                        /*Validar si existe dato*/
                                        /*if (doc.get("Nombre") != null) {

                                        }*/

                                        SQLiteDatabase db = datab.getWritableDatabase();

                                        Cursor fila = db.rawQuery("select * from user WHERE uid = '"+String.valueOf(doc.getId())+"'", null);

                                        Log.i("Total de datos", String.valueOf(fila.getCount()));

                                        if (fila.moveToFirst())
                                        {
//                                            Log.i("Data", fila.getString(0));
//                                            Log.i("Nombre", fila.getString(1));
//                                            Log.i("Telefono", fila.getString(2));
//                                            Log.i("UID", fila.getString(3));
//                                            Log.i("Tipo_user", fila.getString(5));
//                                            Log.i("Logueado", fila.getString(7));

                                            /*Declaracion de session */
                                            SharedPreferences settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

                                            SharedPreferences.Editor editor;
                                            editor = settings.edit();
                                            editor.putString("Nombreusuario" , fila.getString(1));
                                            editor.putString("Telefonousuario" , fila.getString(2));
                                            editor.putString("UIDusuario" , fila.getString(3));
                                            editor.putString("Tipousuario" , fila.getString(5));
                                            editor.putString("Logueadousuario" , fila.getString(7));

                                            editor.apply();

                                            /*Validar si el usuario esta logueado*/
                                            /*if (Boolean.parseBoolean(fila.getString(7)))
                                            {
                                                Log.i("Logueado", "true");
                                            }
                                            else
                                            {
                                                Log.i("Logueado", "false");
                                            }*/

                                        }
                                        else
                                        {
                                            datab.addUser(doc.getString("Nombre"), doc.getString("Telefono"), String.valueOf(doc.getId()), doc.getString("Tipo_user"), "true");
                                        }

                                        db.close();

                                    }
                                    /*Mandar a vista*/
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);

                                } else {
                                    Log.i("Error", "No existe usuario registrado");
                                }

                                waitingDialog.dismiss();

                            }
                        });
            }
        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /*Mostrar alert*/
        dialog.show();

    }
}
