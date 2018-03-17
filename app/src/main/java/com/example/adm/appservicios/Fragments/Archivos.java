package com.example.adm.appservicios.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adm.appservicios.Activity.SolicitarServicioActivity;
import com.example.adm.appservicios.Adapters.Adapter_docs;
import com.example.adm.appservicios.Helpers.UploadCitaImage;
import com.example.adm.appservicios.Helpers.UploadInfoImage;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Doc;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

import static android.content.Context.MODE_PRIVATE;

public class Archivos extends Fragment {

    CardView view1, view2;
    TextView textoTitle, textoDescripcion;
    Button btnEnviar, btnIdOf, btnTitulo, btnPerfil, btnPhoto;

    Boolean data1, data2, data3, data4;
    String url1, url2, url3, url4, IDService;
    LinearLayout linear1;

    private Bitmap bitmap;
    private ImageView data_base_doc1;
    private ImageView data_base_doc2;
    private ImageView data_base_doc3;
    private ProgressDialog progressDialog;
    private Adapter_docs adapter;

    /*Arraylsit image*/
    List<String> list;
    List<String> list2;

    /*variable url para imagen*/
    Uri FilePathUri;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    /*Firebase*/
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseStorage storage;

    public Archivos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Declaracion de session */
        settings = this.getActivity().getSharedPreferences("sesion_user", Context.MODE_PRIVATE);
        Log.i("Session UID ", settings.getString("UIDusuario",""));

        initFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_archivos, container, false);

        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*Inicializacion de arraylist image*/
        list = new ArrayList<String>();
        list2 = new ArrayList<String>();

        /*Inicializacion de banderas para agregar url de fotos*/
        data1 = false;
        data2 = false;
        data3 = false;
        data4 = false;

        url1 = "";
        url2 = "";
        url3 = "";
        url4 = "";

        IDService = "";

        view1 = getView().findViewById(R.id.card_view_header);
        view2 = getView().findViewById(R.id.card_view_item);
        textoTitle = getView().findViewById(R.id.card_item_textView_tittle);
        textoDescripcion = getView().findViewById(R.id.card_item_textView_description);
        btnEnviar = getView().findViewById(R.id.btn_send_docs);

        btnIdOf     = getView().findViewById(R.id.identificacion_ofi);
        btnTitulo   = getView().findViewById(R.id.photo_titulo);
        btnPerfil   = getView().findViewById(R.id.photo_perfil);
        btnPhoto    = getView().findViewById(R.id.photo);
        linear1     = getView().findViewById(R.id.linear1);

        /*Se oculta cardview*/
        view2.setVisibility(View.GONE);
        linear1.setVisibility(View.VISIBLE);

        /*Se consulta si tiene citas el usuario logueado*/
        db.collection("citas")
            .whereEqualTo("idusuario", settings.getString("UIDusuario",""))
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                    /*Validar si hay registro*/
                    if (value.size() > 0) {

                        view2.setVisibility(View.VISIBLE);
                        linear1.setVisibility(View.GONE);
                        view1.setVisibility(View.GONE);

                        /*Recorrido de datos*/
                        for (DocumentSnapshot doc : value)
                        {
                            /*Agregar a array para mostrar alertdialog*/
                            textoTitle.setText(doc.getString("Tipo_perfil"));
                            textoDescripcion.setText(doc.getString("Estatus"));

                        }

                    } else {
                        Log.i("Error", "No existen citas para el usuario logueado");
                    }

                }
            });

        /*Listener click in cardview1*/
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setTitle("Selecciona profesion:");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);

                db.collection("Oficios_trabajadores")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        /*Validar si hay registro*/
                        if (value.size() > 0) {

                            /*Recorrido de datos*/
                            for (DocumentSnapshot doc : value) {

                                /*Agregar a array para mostrar alertdialog*/
                                arrayAdapter.add(doc.getString("Nombre"));

                            }

                        } else {
                            Log.i("Error", "No existen oficios de trabajador");
                        }

                        }
                    });


                /*Cancelar alerdialog*/
                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                /*Opcion seleccionada*/
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    String strName = arrayAdapter.getItem(which);
                    textoTitle.setText(strName);
                    view2.setVisibility(View.VISIBLE);
                    }
                });
                builderSingle.show();
            }
        });

        //Cargar Foto Identificacion
        btnIdOf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImagePicker.pickImage(Archivos.this, "Sube una foto:");

                final int PICK_IMAGE_REQUEST = 71;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Sube una foto"), PICK_IMAGE_REQUEST);

                data1 = true;
            }
        });

        //Cargar Foto Titulo
        btnTitulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImagePicker.pickImage(Archivos.this, "Sube una foto:");

                final int PICK_IMAGE_REQUEST = 71;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Sube una foto"), PICK_IMAGE_REQUEST);

                data2 = true;
            }
        });

        //Cargar Foto Perfil
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImagePicker.pickImage(Archivos.this, "Sube una foto:");

                final int PICK_IMAGE_REQUEST = 71;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Sube una foto"), PICK_IMAGE_REQUEST);

                data3 = true;
            }
        });

        //Cargar Foto Photo
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImagePicker.pickImage(Archivos.this, "Sube una foto:");

                final int PICK_IMAGE_REQUEST = 71;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Sube una foto"), PICK_IMAGE_REQUEST);

                data4 = true;
            }
        });

        /*Click boton enviar*/
        btnEnviar.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("Mandando datos", "a profesion");

                if (TextUtils.isEmpty(textoTitle.getText().toString()))
                {
                    Snackbar.make(getView(), "Por favor selecciona una profesion", Snackbar.LENGTH_SHORT)
                            .show();

                    return;
                }

//                Log.i("Nueva cita", "creando...");
                Map<String, Object> cita = new HashMap<>();
                cita.put("idusuario", settings.getString("UIDusuario",""));
                cita.put("Telefono", settings.getString("Telefonousuario",""));
                cita.put("Tipo_perfil", textoTitle.getText().toString());
                cita.put("Nombre_usuario", settings.getString("Nombreusuario",""));
                cita.put("Estatus", "Pendiente");
                cita.put("Codigo_activicion", "");
                cita.put("Documentos_recibidos", "");
                cita.put("Fecha", "");
                cita.put("Hora", "");
                cita.put("Key_docs", "");
                cita.put("ubi_lat", "");
                cita.put("ubi_lng", "");

                /*Guardar en base de Datos*/
                final SpotsDialog waitingDialog = new SpotsDialog(getActivity());
                waitingDialog.show();

                // Agregar nuevo documento a Base de Datos
                db.collection("citas")
                        .add(cita)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("Informacion", "Solicitud enviada" + documentReference.getId());
                                IDService = documentReference.getId();

                                /*Validacion de url diferente de vacio*/
                                if (url1 != ""){
                                    String idimage = UUID.randomUUID().toString();
                                    list.add(String.valueOf(idimage));
                                    list2.add("idenfiticacion");
                                    sendImage(url1, "idenfiticacion", idimage);
                                }

                                if (url2 != ""){
                                    String idimage = UUID.randomUUID().toString();
                                    list.add(String.valueOf(idimage));
                                    list2.add("titulo");
                                    sendImage(url2, "titulo", idimage);
                                }

                                if (url3 != ""){
                                    String idimage = UUID.randomUUID().toString();
                                    list.add(String.valueOf(idimage));
                                    list2.add("perfil");
                                    sendImage(url3, "perfil", idimage);
                                }

                                if (url4 != ""){
                                    String idimage = UUID.randomUUID().toString();
                                    list.add(String.valueOf(idimage));
                                    list2.add("photo");
                                    sendImage(url4, "photo", idimage);
                                }
                                /*Validacion de url diferente de vacio*/

                                /*Actualizar cita documentos y keydocs*/
                                DocumentReference contact = db.collection("citas").document(IDService);
                                contact.update("Documentos_recibidos", list);
                                contact.update("Key_docs", list2)
                                    .addOnSuccessListener(new OnSuccessListener < Void > () {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getActivity(), "Updated Successfully",
                                                    Toast.LENGTH_SHORT).show();
                                            /*Inicializacion de arraylist image*/
                                            list    = new ArrayList<String>();
                                            list2   = new ArrayList<String>();

                                            url1 = "";
                                            url2 = "";
                                            url3 = "";
                                            url4 = "";
                                        }
                                    });

                                waitingDialog.dismiss();
                                Toast.makeText(getActivity(), "Solicitud enviada", Toast.LENGTH_SHORT).show();

                                /*TextView reset*/
                                textoTitle.setText("");
                                /*Se oculta cardview*/
                                view2.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("informacion", "Error adding document", e);
                                waitingDialog.dismiss();

                                Toast.makeText(getActivity(), "Ocurrio un error intentrar mas tarde", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    /*Inicializacion de Firebase*/
    private void initFirebase() {
        FirebaseApp.initializeApp(getActivity());

        db                  = FirebaseFirestore.getInstance();
        storage             = FirebaseStorage.getInstance();
        storageReference    = storage.getReference();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*Asignacion de url de fotos a variables*/
        if (data != null){
            /*Se obtiene direccion para carga de imagen*/
            FilePathUri = data.getData();
//            Log.i("image uri: " , String.valueOf(FilePathUri));

            if (data1){
                Log.i("Foto de identifica: " , String.valueOf(FilePathUri));
                url1 = String.valueOf(FilePathUri);
                data1 = false;
            }

            if (data2){
                Log.i("Foto de identifica: " , String.valueOf(FilePathUri));
                url2 = String.valueOf(FilePathUri);
                data2 = false;
            }

            if (data3){
                Log.i("Foto de identifica: " , String.valueOf(FilePathUri));
                url3 = String.valueOf(FilePathUri);
                data3 = false;
            }

            if (data4){
                Log.i("Foto de photo: " , String.valueOf(FilePathUri));
                url4 = String.valueOf(FilePathUri);
                data4 = false;
            }
        }

    }

    /*Function para subir foto a servidor*/
    private void sendImage(String url, final String nombre, final String idimage){

        StorageReference ref = storageReference.child("All_Image_Uploads/"+ idimage);
        ref.putFile(Uri.parse(url))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        progressDialog.dismiss();
//                        Toast.makeText(SolicitarServicioActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        String name = taskSnapshot.getMetadata().getName();
                        String url = taskSnapshot.getDownloadUrl().toString();

                        Log.i("Uri upload image", "Uri: " + url);
                        Log.i("Name upload image", "Name: " + name);

                        UploadCitaImage info = new UploadCitaImage(nombre, url, settings.getString("UIDusuario",""), IDService, idimage);
                        db.collection("images").add(info);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(SolicitarServicioActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
//                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });

    }
}
