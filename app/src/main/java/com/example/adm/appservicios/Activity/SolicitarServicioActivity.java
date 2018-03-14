package com.example.adm.appservicios.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adm.appservicios.Database.SQLiteHandler;
import com.example.adm.appservicios.Helpers.Address;
import com.example.adm.appservicios.Helpers.CheatSheet;
import com.example.adm.appservicios.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mvc.imagepicker.ImagePicker;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class SolicitarServicioActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private CardView card_servicio;
    private TextView txtView_Servicio, txtView_Address;
    EditText editText_descripcion, editText_min, editText_max;
    private ImageView imageView_addphoto, ic_save_on_adddress, iconHelpService, iconHelpDescription, iconHelpPhoto, iconHelpLocation, iconHelpMoney;
    private Button button_agregar_ubicacion, button_ubicacion_existente, button_enviar;
    TextView editText_address, editText_tituloAddress;
    private ProgressDialog pDialog;
    Double latpos, lngpos;
    private Bitmap bitmap;

    String descripcionString, tituloString;

    final static int MY_PERMISSION_FINE_LOCATION = 101;
    final static int PLACE_PICKER_REQUEST = 1;
    GoogleApiClient mClient;
    Context mContext;

    private Uri filePath;

    //LinearLayout
    LinearLayout linearLayour_horizontal;

    ExpandableLayout expandableLayout0;

    /*Firebase*/
    FirebaseFirestore db;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_servicio);

        // Inicializacion de Firebase database
        initFirebase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Solicitar Servicio");

        linearLayour_horizontal 	= (LinearLayout) findViewById(R.id.linearLayour_horizontalPhotos);
        card_servicio 				= (CardView) findViewById(R.id.cardView_servicio);
        txtView_Servicio 			= (TextView) findViewById(R.id.txtView_Servicio);
        txtView_Address 			= (TextView) findViewById(R.id.attach_address);
        editText_descripcion 		= (EditText) findViewById(R.id.editText_descripcion_del_problema);
        imageView_addphoto 			= (ImageView) findViewById(R.id.imageView_add_photo);
        button_agregar_ubicacion 	= (Button) findViewById(R.id.button_AgregarUbicacion);
        button_ubicacion_existente 	= (Button) findViewById(R.id.button_UbicacionesExistentes);
        editText_min 				= (EditText) findViewById(R.id.editText_Min);
        editText_max 				= (EditText) findViewById(R.id.editText_Max);
        button_enviar 				= (Button) findViewById(R.id.button_EnviarPedido);
        ic_save_on_adddress 		= (ImageView) findViewById(R.id.ic_save_on_adddress);

        //ICONOS DE AYUDA
        iconHelpService         = (ImageView) findViewById(R.id.ic_tooltipServicio);
        iconHelpDescription     = (ImageView) findViewById(R.id.ic_tooltipDescripcion);
        iconHelpPhoto           = (ImageView) findViewById(R.id.ic_tooltipPhotos);
        iconHelpLocation        = (ImageView) findViewById(R.id.ic_tooltipLocation);
        iconHelpMoney           = (ImageView) findViewById(R.id.ic_tooltipMoney);

        iconHelpPhoto.setVisibility(View.GONE);
        iconHelpService.setVisibility(View.GONE);
        iconHelpLocation.setVisibility(View.GONE);
        iconHelpDescription.setVisibility(View.GONE);
        iconHelpDescription.setVisibility(View.GONE);

        CheatSheet.setup(iconHelpMoney, "No es necesario llenar este campo, pero podría ayudarte a encontrar un servicio a la medida para ti");

        editText_descripcion.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText_descripcion.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText_min.setInputType(InputType.TYPE_CLASS_PHONE);
        editText_min.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText_max.setInputType(InputType.TYPE_CLASS_PHONE);
        editText_max.setImeOptions(EditorInfo.IME_ACTION_DONE);

        expandableLayout0 			= (ExpandableLayout) findViewById(R.id.expandable_layout_0);
        editText_address 			= (TextView) findViewById(R.id.editText_address);
        editText_tituloAddress 		= (TextView) findViewById(R.id.editText_tituloAddress);

        /*Declaracion de session */
        settings = getSharedPreferences("sesion_user", MODE_PRIVATE);
        Log.i("Session UID ", settings.getString("UIDusuario",""));

        //Dialogo de servicio
        card_servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialogEmployee();
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        //button_agregar_ubicacion.setEnabled(false);

        /*Inicializacion de googleapicliente*/
        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        /*Click para agregar nueva direccion*/
        button_agregar_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click en boton", "true");

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SolicitarServicioActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

				/*Intent intent =  new Intent(Activity_Form_Service.this, Activity_Address.class);
				startActivity(intent);*/
            }
        });

        /*Click para guardar direccion*/
        ic_save_on_adddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "click save");
                tituloString = editText_tituloAddress.getText().toString();
                descripcionString = editText_address.getText().toString();

//                Log.i("titulo", tituloString);
//                Log.i("descripcion", descripcionString);
//                Log.i("latpos", String.valueOf(latpos));
//                Log.i("lngpos", String.valueOf(lngpos));

                Log.i("Nueva direccion", "creando...");
                Map<String, Object> address = new HashMap<>();
                address.put("User", settings.getString("UIDusuario",""));
                address.put("Titulo", tituloString);
                address.put("Direccion", descripcionString);
                address.put("Lat", String.valueOf(latpos));
                address.put("Lng", String.valueOf(lngpos));

                /*Guardar en base de Datos*/
                final SpotsDialog waitingDialog = new SpotsDialog(SolicitarServicioActivity.this);
                waitingDialog.show();

                // Agregar nuevo documento a Base de Datos
                db.collection("address")
                        .add(address)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("Informacion", "Direccion agregada a base de datos" + documentReference.getId());
                                waitingDialog.dismiss();

                                /*Collapse hide*/
                                expandableLayout0.collapse();

                                /*TextView reset*/
                                editText_tituloAddress.setText("");
                                editText_address.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("informacion", "Error adding document", e);
                                waitingDialog.dismiss();
                            }
                        });

            }
        });

        //Asunto de las fotos
        imageView_addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImagePicker.pickImage(SolicitarServicioActivity.this, "Sube una foto:");
                final int PICK_IMAGE_REQUEST = 71;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        /*Seleccionar ubicacion para guardar en base de datos*/
        button_ubicacion_existente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Obtener ubicaciones existentes de base de datos*/
                ListenerRegistration listenerRegistration = db.collection("address")
                        .whereEqualTo("User", settings.getString("UIDusuario",""))
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                //Log.i("sucess", String.valueOf(value.size()));

                                /*Validar si hay registro*/
                                if (value.size() > 0) {

                                    /*Declaracion inicial de arrayadpater*/
                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SolicitarServicioActivity.this, android.R.layout.select_dialog_singlechoice);
                                    final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(SolicitarServicioActivity.this, android.R.layout.select_dialog_singlechoice);

                                    /*Recorrido de datos*/
                                    for (DocumentSnapshot doc : value) {

                                        Log.i("Direccion", doc.getString("Direccion"));

                                        arrayAdapter.add(doc.getString("Direccion"));
                                        arrayAdapter2.add(doc.getString("Lat") + "," + doc.getString("Lng"));

                                    }

                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(SolicitarServicioActivity.this);
                                    builderSingle.setIcon(R.drawable.map_pin);
                                    builderSingle.setTitle("Select One Name:-");

                                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String strName    = arrayAdapter.getItem(which);
                                            final String LatLng     = arrayAdapter2.getItem(which);

                                            Log.i("Position sel " , LatLng);
                                            String[] separated = LatLng.split(",");
                                            Log.i("Position sel " , separated[0]);
                                            Log.i("Position sel " , separated[1]);
                                            latpos = Double.parseDouble(separated[0]);
                                            lngpos = Double.parseDouble(separated[1]);

                                            AlertDialog.Builder builderInner = new AlertDialog.Builder(SolicitarServicioActivity.this);
                                            builderInner.setMessage(strName);
                                            builderInner.setTitle("Direccion Seleccionada");
                                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,int which) {
                                                    txtView_Address.setText(strName);
                                                    dialog.dismiss();
                                                }
                                            });
                                            builderInner.show();
                                        }
                                    });
                                    builderSingle.show();

                                } else {
                                    Log.i("Error", "No existe Direcciones para el usuario");
                                    AlertDialog.Builder builder;
                                    builder = new AlertDialog.Builder(SolicitarServicioActivity.this);
                                    builder.setTitle("Direcciones")
                                            .setMessage("No tienes direcciones disponibles.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with delete
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }

                            }
                        });
            }
        });

        /*Click para guardar servicio en base de datos*/
        button_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click", "Enviar");

                editText_min 				= (EditText) findViewById(R.id.editText_Min);
                editText_max 				= (EditText) findViewById(R.id.editText_Max);

                Log.i("Servicio ", txtView_Servicio.getText().toString());
                Log.i("Descripcion ", editText_descripcion.getText().toString());
                Log.i("Direccion ", txtView_Address.getText().toString());
                Log.i("lat ", String.valueOf(latpos));
                Log.i("lng ", String.valueOf(lngpos));
                Log.i("Min ", editText_min.getText().toString());
                Log.i("Max ", editText_max.getText().toString());

                Log.i("Nuevo servicio", "creando...");
                Map<String, Object> services = new HashMap<>();
                services.put("idusuario", settings.getString("UIDusuario",""));
                services.put("Descripcion", editText_descripcion.getText().toString());
                services.put("Direccion", txtView_Address.getText().toString());
                services.put("Servicio", txtView_Servicio.getText().toString());
                services.put("Min", editText_min.getText().toString());
                services.put("Max", editText_max.getText().toString());
                services.put("Lat", latpos);
                services.put("Lng", lngpos);

                String x = getImages();
                Log.i("image ", x);

                DatabaseReference mDatabase;
// ...
                /*Save database realtime database*/
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//
//                Address user = new Address(settings.getString("UIDusuario",""), txtView_Servicio.getText().toString(), editText_descripcion.getText().toString(), String.valueOf(latpos), String.valueOf(lngpos));

//                mDatabase.child("address").setValue(user);

                /*Guardar en base de Datos*/
                final SpotsDialog waitingDialog = new SpotsDialog(SolicitarServicioActivity.this);
                waitingDialog.show();

                // Agregar nuevo documento a Base de Datos
                db.collection("services")
                        .add(services)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("Informacion", "Servicio agregado a base de datos" + documentReference.getId());
                                waitingDialog.dismiss();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("informacion", "Error adding document", e);
                                waitingDialog.dismiss();
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("onStart", "onStart");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode,data);

        if(bitmap != null){
            iconHelpPhoto.setVisibility(View.GONE);
            ImageView image = new ImageView(getApplicationContext());
            image.setImageBitmap(bitmap);
            float width = getResources().getDimension(R.dimen.image_widht);
            Integer x = Math.round(width);
            image.setLayoutParams(new ActionBar.LayoutParams(x, ActionBar.LayoutParams.MATCH_PARENT));
            View linearlayout = findViewById(R.id.linearLayour_horizontalPhotos);
            ((LinearLayout) linearlayout).addView(image, 0);
        }
        InputStream is = ImagePicker.getInputStreamFromResult(this, requestCode, resultCode, data);
        if (is != null) {
            //textView.setText("Got input stream!");
            try {
                is.close();
            } catch (IOException ex) {
                // ignore
            }
        } else {
            //textView.setText("Failed to get input stream!");
        }

        super.onActivityResult(requestCode, resultCode, data);

        Uri FilePathUri;
        FilePathUri = data.getData();
        Log.i("image uri: " , String.valueOf(FilePathUri));

        /*Location selected */
        if (requestCode == PLACE_PICKER_REQUEST){
            Log.i("Data place_picker: " , "place_picker");
            if (resultCode == RESULT_OK) {
                Log.i("Data RESULT_OK: " , "RESULT_OK");
                Place place = PlacePicker.getPlace(SolicitarServicioActivity.this, data);
                String address = String.format("Place %s",place.getAddress());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                Log.i("address " , address);
                Log.i("latitud " , latitude);
                Log.i("longitud " , longitude);

                latpos = Double.parseDouble(latitude);
                lngpos = Double.parseDouble(longitude);

                editText_address.setText(address);

                expandableLayout0.expand();

            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    /*Inicializacion de Firebase*/
    private void initFirebase() {
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

    }

    public void createDialogEmployee(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_work_black);
        builderSingle.setTitle("¿Qué oficio desempeñas?");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        String [] professions = this.getResources().getStringArray(R.array.services_array);
        for (String pro: professions){
            arrayAdapter.add(pro);
        }
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                onSelecOficio(strName);
                iconHelpService.setVisibility(View.GONE);
            }
        });
        builderSingle.show();
    }

    /*Al Seleccionar oficio se asigna a textview*/
    public void onSelecOficio(String string_oficio){
        txtView_Servicio.setText(string_oficio);
    }

    /*Obtener string de imagen*/
    private String imageToString (Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
    /*Obtener string de imagen*/

    /*Obtener imagenes de array*/
    public String getImages()  {
        int count = linearLayour_horizontal.getChildCount();
        JSONArray imagenes = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for(int i=0; i<count-1; i++) {
            ImageView imageView = (ImageView) linearLayour_horizontal.getChildAt(i);
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            String image = imageToString(bitmap);
            imagenes.put(image);
        }

        try {
            jsonObject.put("fotos", imagenes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String photos_for_send = jsonObject.toString();
        return photos_for_send;
    }
    /*Obtener imagenes de array*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}

/*Permisos de localizacion*/
class UtilityLocation {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Camera permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    android.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
