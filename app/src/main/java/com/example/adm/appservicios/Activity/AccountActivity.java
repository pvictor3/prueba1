package com.example.adm.appservicios.Activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adm.appservicios.Helpers.UploadInfoImage;
import com.example.adm.appservicios.Helpers.UploadProfileImage;
import com.example.adm.appservicios.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mvc.imagepicker.ImagePicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.UUID;

public class AccountActivity extends AppCompatActivity {

    TextView nombreUser, telefonoUser;
    Button btnSend;
    ImageView mProfileImage;

    /*variable url para imagen*/
    Uri FilePathUri;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    /*Firebase*/
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bar_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar Cuenta");

        /*Llamado a funcion para inicializar firebase*/
        initFirebase();

        mProfileImage = (ImageView) findViewById(R.id.imageProfileUSRACT);
        //Para tomar la foto.
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImagePicker.pickImage(AccountActivity.this, "Subir foto:");

                final int PICK_IMAGE_REQUEST = 71;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Sube una foto"), PICK_IMAGE_REQUEST);
            }
        });
        ImagePicker.setMinQuality(600, 600);

        /*Declaracion de session */
        settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

        /*Se obtiene textview de vista*/
        nombreUser      = findViewById(R.id.edit_name);
        telefonoUser    = findViewById(R.id.editText_telefono);
        btnSend         = findViewById(R.id.btn_send_image);

        /*Asignación de textos*/
        nombreUser.setText(settings.getString("Nombreusuario",""));
        telefonoUser.setText(settings.getString("Telefonousuario",""));

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
                                Log.i("Nombre real time", doc.getString("Nombre"));
                                /*Asignación de textos*/
                                nombreUser.setText(doc.getString("Nombre"));
                                telefonoUser.setText(doc.getString("Telefono"));

                                SharedPreferences.Editor editor;
                                editor = settings.edit();
                                editor.putString("Nombreusuario" , doc.getString("Nombre"));
                                editor.putString("Telefonousuario" , doc.getString("Telefono"));

                                if (doc.getString("Image_user") != "")
                                {
                                    // Asignacion de imagen de perfil a imageView
                                    StorageReference storageReference = storage.getReferenceFromUrl(doc.getString("Image_user"));

                                    Glide.with(AccountActivity.this)
                                            .using(new FirebaseImageLoader())
                                            .load(storageReference)
                                            .into(mProfileImage);
                                }

                            }

                        }

                    }
                });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(nombreUser.getText().toString(), telefonoUser.getText().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        /*Se obtiene direccion para carga de imagen*/
        if (data != null){
            FilePathUri = data.getData();
            Log.i("image uri: " , String.valueOf(FilePathUri));

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Profile/"+ UUID.randomUUID().toString());
            ref.putFile(Uri.parse(String.valueOf(FilePathUri)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AccountActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            String name = taskSnapshot.getMetadata().getName();
                            String url = taskSnapshot.getDownloadUrl().toString();

                            Log.i("Uri upload image", "Uri: " + url);
                            Log.i("Name upload image", "Name: " + name);

                            UploadProfileImage info = new UploadProfileImage(name, url, settings.getString("UIDusuario",""));
                            db.collection("images").add(info);

                            DocumentReference contact = db.collection("users").document(settings.getString("UIDusuario",""));
                            contact.update("Image_user", url)
                                    .addOnSuccessListener(new OnSuccessListener < Void > () {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AccountActivity.this, "Imagen Subida con exito.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AccountActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Inicializacion de Firebase*/
    private void initFirebase() {
        FirebaseApp.initializeApp(this);

        db                  = FirebaseFirestore.getInstance();
        storage             = FirebaseStorage.getInstance();
        storageReference    = storage.getReference();

    }

    private  void updateData(String nombre, String telefono)
    {
        DocumentReference contact = db.collection("users").document(settings.getString("UIDusuario",""));
        contact.update("Nombre", nombre);
        contact.update("Telefono", telefono)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /*Se muestra toast success*/
                        Toast.makeText(AccountActivity.this, "Se actualizaron datos de usuario",
                                Toast.LENGTH_SHORT).show();

                        /*Inicializacion de arraylist image*/
                        Log.i("Data success", String.valueOf(aVoid));

                    }
                });
    }
}
