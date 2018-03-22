package com.example.adm.appservicios.Activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import com.example.adm.appservicios.Common.Common;
import com.example.adm.appservicios.Remote.IGoogleAPI;
import com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.adm.appservicios.Manifest;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.workers;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ViajeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    MapView mapView;
    LatLng pointinit;

    /*Firebase*/
    FirebaseFirestore db;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    String idservice, idusuario, endAddress;
    Double latinit, lnginit;

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    GeoFire geoFire;
    Marker mMarker;

    private List<LatLng> polyLineList;
    private Marker pickupLocationMarker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition, currentsPosition;
    private int index, next;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, getPolyline;
    private IGoogleAPI mService;

    /*Incrementar variables*/
    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
              if (index < polyLineList.size() - 1)
              {
                 index ++;
                 next = index + 1;
              }

              if (index < polyLineList.size() - 1)
              {
                  startPosition = polyLineList.get(index);
                  endPosition   = polyLineList.get(next);
              }

              final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0 , 1);
              valueAnimator.setDuration(3000);
              valueAnimator.setInterpolator(new LinearInterpolator());
              valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                  @Override
                  public void onAnimationUpdate(ValueAnimator animation) {
                      /**/
                      v = valueAnimator.getAnimatedFraction();
                      lng = v * endPosition.longitude + (1 + v) * startPosition.longitude;
                      lat = v * endPosition.latitude + (1 + v) * startPosition.latitude;

                      Log.i("New v ", String.valueOf(v));
                      Log.i("New startPosition ", String.valueOf(startPosition));
                      Log.i("New endPosition ", String.valueOf(endPosition));
                      Log.i("New latlng ", String.valueOf(lat) + "," + String.valueOf(lng));

                      LatLng newPos = new LatLng(latinit, lnginit);
                      Log.i("New Pos ", String.valueOf(newPos));

                      mMarker.setPosition(newPos);
                      mMarker.setAnchor(0.5f, 0.5f);
                      mMarker.setRotation(getBearing(startPosition, newPos));
                      mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                              new CameraPosition.Builder()
                              .target(newPos)
                              .zoom(15.0f)
                              .build()
                      ));
                  }
              });

              valueAnimator.start();
//              handler.postDelayed(this, 5000);
        }
    };

    private float getBearing(LatLng startPosition, LatLng endPosition) {
        double lat = Math.abs(startPosition.latitude - endPosition.latitude);
        double lng = Math.abs(startPosition.longitude - endPosition.longitude);

        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) (90 - Math.toDegrees(Math.atan(lng / lat)) + 90);
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) (90 - Math.toDegrees(Math.atan(lng / lat)) + 270);
        return - 1;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Viaje");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Obteniendo tu ubicación.", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                mMap.clear();

                /*Actualizar*/
                startLocationUpdates();
//                displayLocation();
                getDirection();
            }
        });

        /*Declaracion de session */
        settings = getSharedPreferences("sesion_user", MODE_PRIVATE);

        /*Se recibe id de servicio seleccionado*/
        final Intent intent = getIntent();
        idservice = intent.getStringExtra("idservice");
        idusuario = intent.getStringExtra("idusuario");

//        Log.i("idservice ", idservice);
//        Log.i("idusuario ", idusuario);

        // Inicializacion de Firebase database
        initFirebase();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync((OnMapReadyCallback) this);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);

        /*Inicializacion de arraylist*/
        polyLineList = new ArrayList<>();

        mService = Common.getGoogleAPI();

//        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference("drivers");
//        geoFire = new GeoFire(drivers);

        /*Obtener servicios de Trabajador*/
        DocumentReference docRef = db.collection("services").document(idservice);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Log.i("Datos Descripcion " , document.getString("Descripcion"));
                    Log.i("Datos Lat " , String.valueOf(document.getDouble("Lat")));
                    Log.i("Datos Lng " , String.valueOf(document.getDouble("Lng")));

                    latinit = document.getDouble("Lat");
                    lnginit = document.getDouble("Lng");

                    pointinit = new LatLng(document.getDouble("Lat"), document.getDouble("Lng"));

                    setUpLocation();
//                    getDirection();

                } else {
                    Log.d("Failed ", "get failed with ", task.getException());
                }
            }
        });

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

    /*Inicializacion de Firebase*/
    private void initFirebase() {
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                /*Intent to Main, pass parameter id = 2*/
                Intent intent = new Intent(ViajeActivity.this, MisServiciosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Mapa esta ready*/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        setCurretPosition();

    }

    private void getDirection()
    {
        currentsPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        String requestApi = null;
        try{
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentsPosition.latitude + "," + currentsPosition.longitude + "&" +
                    "destination=" + latinit  + "," + lnginit + "&" +
                    "key=AIzaSyCAaLooco7bxM5IE9PhzYeA5RbEtPhIp6M";

            Log.i("url peticion map " , requestApi);

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");

                                for (int i = 0; i<jsonArray.length(); i++)
                                {
                                    JSONObject route = jsonArray.getJSONObject(i);

                                    JSONArray legs = route.getJSONArray("legs");

                                    for (int e = 0; e<legs.length(); e++)
                                    {
                                        JSONObject leg = legs.getJSONObject(e);

                                        /*end_address*/
                                        endAddress = "Servicio: " + leg.getString("end_address");

                                        JSONObject distance = leg.getJSONObject("distance");

                                        Log.i("Distancia ", distance.getString("value"));

                                        if (Integer.parseInt(distance.getString("value")) < 150)
                                        {
                                            Log.i("Distancia ", "ya esta serca");

                                        }
                                    }

                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");

                                    polyLineList = decodePoly(polyline);

                                }

                                List<LatLng> directionList = new ArrayList<LatLng>();

                                //Adjusting bounds
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng:polyLineList) {
//                                    Log.i("points ", String.valueOf(latLng));
                                    builder.include(latLng);
                                    directionList.add(latLng);
                                }
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);

                                mMap.animateCamera(mCameraUpdate);

                                final Polyline greyPolyline, blackPolyline;

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(8);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(directionList);
                                greyPolyline = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                /*Se obtiene la ultima posicion del servicio*/
                                mMap.addMarker(new MarkerOptions()
                                    .position(polyLineList.get(polyLineList.size()-1))
                                    .title("Servicio")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker)));

                                /*Animation*/
                                ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0 , 100);
                                polyLineAnimator.setDuration(2000);
                                polyLineAnimator.setInterpolator(new LinearInterpolator());
                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        List<LatLng> points = greyPolyline.getPoints();
                                        int percentValue = (int) animation.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue/100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);

                                    }
                                });
                                polyLineAnimator.start();

                                mMarker = mMap.addMarker(new MarkerOptions().position(currentsPosition)
                                            .flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                            .title("Tú"));

                                /*centra el mapa en las coordenadas dadas*/
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentsPosition, 15.0f));

                                Log.i("currentsPosition", String.valueOf(currentsPosition));

                                handler = new Handler();
                                index = 1;
                                next = 1;
//                                handler.postDelayed(drawPathRunnable, 3000);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(ViajeActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setCurretPosition(){
        Log.i("Vista creada", " setCurretPosition");

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pointinit, 11);
        mMap.animateCamera(cameraUpdate);

    }

    private void setUpLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        }
        else
        {
            if (checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();

            }
        }
    }

    private void createLocationRequest() {

        Log.i("createLocationRequest" , "true");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            }
            else {
                Toast.makeText(this, "Dispositivio no soportado", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

    }

    private void displayLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null)
        {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

//            geoFire.setLocation(settings.getString("UIDusuario",""), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
//                @Override
//                public void onComplete(String key, DatabaseError error) {

                    /*Limpiar mapa de markers*/
//                    if (mMarker != null)
//                        mMarker.remove();

                    /*Mostar mi ubicacion*/
//                    mMarker = mMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
//                        .position(new LatLng(latitude, longitude))
//                        .title("You position"));

                    mMarker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker))
                        .position(pointinit)
                        .title("Servicio"));

                    /*centra el mapa en las coordenadas dadas*/
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

                    /*rotar marcador*/
//                    rotateMarker(mCurrent, -360, mMap);

//                }
//            });
        }
        else
        {
            Toast.makeText(this, "No se puede obtener tu ubicacion.", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();

                    }
                }
        }
    }

    private void stopLocationUpdates()
    {
        mMarker.remove();
        mMap.clear();
//        handler.removeCallbacks(drawPathRunnable);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("onlocationchanged ", " true");
        mLastLocation = location;
//        displayLocation();

        if (mMarker != null)
        {
            mMarker.remove();
            mMap.clear();
            getDirection();
        }

    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        displayLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
