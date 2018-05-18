package com.example.adm.appservicios.Fragments;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.adm.appservicios.Adapters.CustomInfoWindowAdapter;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Servicios_worker;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IndexFragment extends Fragment implements OnMapReadyCallback {

    int contador = 0;
    MapView mapView;
    GoogleMap mMap;
    AutoCompleteTextView search_EditText;

    private static final int MY_PERMISSSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationListener mLocationRequest;
    private GoogleApiClient mGoogleApiCliente;
    private Location mLastLocation;

    private static int UPDATE_INTVERVAL = 5000;
    private static  int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    GeoFire geoFire;

    /*Firebase*/
    FirebaseFirestore db;
    MarkerOptions markerOptions = new MarkerOptions();

    //Arreglo de marcadores
    ArrayList<String> userMarkers = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_index, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync((OnMapReadyCallback) this);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        Log.i("Vista creada", " index");

        search_EditText = (AutoCompleteTextView) getView().findViewById(R.id.main_search);
        search_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        search_EditText.setHint("¿Qué quiero?");

        /*El ASUNTO DEL FLOATIN BUTTON*/
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Autocomplete text ", search_EditText.getText().toString());
                if(search_EditText.getText().toString().equals("Usuario")){
                    Toast.makeText(getActivity(), R.string.warning_invalid_search,Toast.LENGTH_SHORT).show();
                }else{
                    getDatas(search_EditText.getText().toString());
                }

            }
        });

        // Inicializacion de Firebase database
        initFirebase();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("Vista creada", " onMapReady");
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        setCurretPosition();
        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(getContext());
        mMap.setInfoWindowAdapter(customInfoWindow);
    }

    public void setCurretPosition(){
        Log.i("Vista creada", " setCurretPosition");
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(19.33978502, -99.19086277 ), 10);
        mMap.animateCamera(cameraUpdate);
    }

    /*Inicializacion de Firebase*/
    private void initFirebase() {

        db = FirebaseFirestore.getInstance();
    }

    public void getDatas(String value){

        /*Obtener ubicaciones existentes de base de datos*/
        ListenerRegistration listenerRegistration = db.collection("users")
                .whereEqualTo("Tipo_user", value)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        /*Validar si hay registro*/
                        if (value.size() > 0) {

                            int counter = 0;
                            /*Recorrido de datos*/
                            final ArrayList<String> idSearch = new ArrayList<>();
                            final ArrayList<LatLng> points = new ArrayList<>();
                            final ArrayList<String> names = new ArrayList<>();
                            for (DocumentSnapshot doc : value) {

                                if (doc.getString("Lat") != null)
                                {
                                    points.add(new LatLng(Double.parseDouble(doc.getString("Lat")), Double.parseDouble(doc.getString("Lng"))));
                                    names.add(doc.getString("Nombre"));
                                    //placeMarker(new LatLng(Double.parseDouble(doc.getString("Lat")), Double.parseDouble(doc.getString("Lng"))), counter, doc.getString("Nombre"), doc.getId());
                                    counter ++;
                                    idSearch.add(doc.getId());
                                }
                            }
                            contador = 0;
                            obtenerDirecciones(idSearch, points, names);
                        } else {
                            /*No existen servicios registrados por usuario*/
                            Log.i("Error", "No existe usuario para la busqueda dada.");
                            mMap.clear();
                            Toast.makeText(getActivity(), "No existen datos para la busqueda dada.",
                                    Toast.LENGTH_LONG).show();

                        }

                    }
                });


    }

    public void obtenerDirecciones(final ArrayList<String> ids, final ArrayList<LatLng> points, final ArrayList<String> names){
        if(contador == ids.size()){
            return;
        }
        db.collection("address").whereEqualTo("User", ids.get(contador))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(DocumentSnapshot document : task.getResult()){
                        String address = document.getString("Direccion");
                        Log.d("firestore", "address alcanzada");
                        placeMarker(points.get(contador),contador,names.get(contador),address);
                    }
                    contador++;
                    obtenerDirecciones(ids, points, names);
                }
                else{
                    Log.d("firestore", "get failed with ", task.getException());
                }
            }
        });
    }

    public void placeMarker (LatLng point, int pos, String Nombre, String address) {
        Log.i("Vista creada", " placeMarker");
        if (mMap != null) {
            if (pos == 0){
                mMap.clear();
            }

            // Setting latitude and longitude for the marker
            markerOptions.position(point);
            markerOptions.title(Nombre);
            markerOptions.snippet(address);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
            Log.d("firestore","Nombre = " + Nombre);
/*
            db.collection("address").whereEqualTo("User", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot document : task.getResult()){
                            String address = document.getString("Direccion");
                            markerOptions.snippet(address);
                            mMap.addMarker(markerOptions);

                            Log.d("firestore", "address alcanzada");
                        }

                    }
                    else{
                        Log.d("firestore", "get failed with ", task.getException());
                    }
                }
            });*/
            // Adding marker on the Google Map
            mMap.addMarker(markerOptions);
        }
    }
}
