package com.example.adm.appservicios.Activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adm.appservicios.Common.Common;
import com.example.adm.appservicios.Helpers.GeofenceTransitionsIntentService;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.Remote.IGoogleAPI;
import com.firebase.geofire.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /*Variables para la localizacion*/
    public static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private GoogleApiClient googleApiClient;
    private Location userLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 700;
    private long FASTEST_INTERVAL = 500;

    /*Variables para el mapa*/
    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    /*Variables para polyline*/
    private String destination;
    private List<LatLng> polylineList;
    private Marker marker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition;
    private int index, next;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;
    private LatLng myLocation;
    IGoogleAPI mService;

    /*Variables para notificaciones*/
    public static final String CHANNEL_ID = "NotificacionesMapa";
    public static final int notificationId = 1;

    /*Animation car*/
    private boolean isFirstPosition = true;

    /*DistanceTo*/
    Location mDestiny;

    /*Geofences*/
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    public static final String geofenceId = "DestinationGeofence";
    public static final int RADIUS_IN_METERS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        polylineList = new ArrayList<>();

        mService = Common.getGoogleAPI();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /*Se obtiene el destino*/
        Intent intent = getIntent();
        destination = intent.getStringExtra("direccion").replace(" ", "+");
        Log.d("mapsActivity", "onCreate: Direccion: " + destination);

        /*Creacion de canal de notificaciones*/
        createNotificationChannel();

        /*Creacion de cliente para geofences*/
        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }
    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("mapsActivity", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        googleApiClient.disconnect();
        mGeofencingClient.removeGeofences(getGeofencePendingIntent());
        Log.d("mapsActivity", "onDestroy: ");
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        Log.d("geofences", "getGeofencingRequest: ");
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("geofences", "getGeofencePendingIntent: ");
        return mGeofencePendingIntent;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_MAX;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            //Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.clear();
        // Add a marker in User Coordinates and move the camera
        final LatLng userCoordinates = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(userCoordinates).title("My location"));
        marker = mMap.addMarker(new MarkerOptions()
                .position(userCoordinates)
                .flat(true)
                .title("Posicion actual")
                .anchor(0.5f, 0.5f)
                .rotation(-90.0f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_top_view))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoordinates));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(17)
                .bearing(30)
                .tilt(45)
                .build()));
        String requestUrl = null;
        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + userCoordinates.latitude + "," + userCoordinates.longitude + "&" +
                    "destination=" + destination + "&" +
                    //"destination="+19.392114+","+-99.165987+"&"+ Direccion de prueba
                    "key=" + getResources().getString(R.string.google_directions_key);
            Log.d("URL", requestUrl);
            mService.getDataFromGoogleApi(requestUrl)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");

                                    polylineList = decodePoly(polyline);

                                    //Adjusting bounds
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    for (LatLng latLng : polylineList) {
                                        builder.include(latLng);
                                    }
                                    LatLngBounds bounds = builder.build();
                                    CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                    mMap.animateCamera(mCameraUpdate);

                                    polylineOptions = new PolylineOptions();
                                    polylineOptions.color(Color.GRAY);
                                    polylineOptions.width(5);
                                    polylineOptions.startCap(new SquareCap());
                                    polylineOptions.endCap(new SquareCap());
                                    polylineOptions.jointType(JointType.ROUND);
                                    polylineOptions.addAll(polylineList);
                                    greyPolyline = mMap.addPolyline(polylineOptions);

                                    blackPolylineOptions = new PolylineOptions();
                                    blackPolylineOptions.color(Color.BLACK);
                                    blackPolylineOptions.width(10);
                                    blackPolylineOptions.startCap(new SquareCap());
                                    blackPolylineOptions.endCap(new SquareCap());
                                    blackPolylineOptions.jointType(JointType.ROUND);
                                    blackPolylineOptions.addAll(polylineList);
                                    blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                    LatLng destinyCoordinates = polylineList.get(polylineList.size() - 1);
                                    mDestiny = new Location("My destiny");
                                    mDestiny.setLatitude(destinyCoordinates.latitude);
                                    mDestiny.setLongitude(destinyCoordinates.longitude);

                                    mMap.addMarker(new MarkerOptions()
                                            .position(destinyCoordinates).title("My destiny")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_work_place))
                                    );

                                    /*Creacion y adicion de geofences*/
                                    mGeofenceList.add(new Geofence.Builder()
                                            .setRequestId(geofenceId)
                                            .setCircularRegion(
                                                    mDestiny.getLatitude(),
                                                    mDestiny.getLongitude(),
                                                    RADIUS_IN_METERS
                                            )
                                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                                            .build()
                                    );

                                    mMap.addCircle(new CircleOptions()
                                            .center(destinyCoordinates)
                                            .radius(RADIUS_IN_METERS)
                                            .strokeColor(Color.BLUE)
                                            .fillColor(0x220000FF)
                                            .strokeWidth(5.0f)
                                    );

                                    Log.d("geofences", "onResponse: GEOFENCE ADDED TO LIST");

                                    mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("geofences", "onSuccess: Geofences Added");
                                                //Intent intent = new Intent(MapsActivity.this, GeofenceTransitionsIntentService.class);
                                                //startService(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                                Log.d("geofences", "onFailure: " );
                                            }
                                        });

                                    startLocationUpdates();
                                    /*
                                    //Animator
                                    final ValueAnimator polylineAnimator = ValueAnimator.ofInt(0,100);
                                    polylineAnimator.setDuration(2000);
                                    polylineAnimator.setInterpolator(new LinearInterpolator());
                                    polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            List<LatLng> points = greyPolyline.getPoints();
                                            int percentValue = (int) animation.getAnimatedValue();
                                            int size = points.size();
                                            int newPoints = (int) (size * (percentValue / 100.0f));
                                            List<LatLng> p = points.subList(0, newPoints);

                                            blackPolyline.setPoints(p);
                                        }
                                    });
                                    polylineAnimator.start();

                                    //Add marker
                                    marker = mMap.addMarker(new MarkerOptions().position(sydney)
                                            .flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue))
                                    );

                                    //Car moving
                                    handler =  new Handler();
                                    index = -1;
                                    next = 1;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(index < polylineList.size() - 1){
                                                index++;
                                                next = index + 1;
                                            }
                                            if(index < polylineList.size() - 1){
                                                startPosition = polylineList.get(index);
                                                endPosition = polylineList.get(next);
                                            }
                                            final ValueAnimator valueAnimator = ValueAnimator.ofInt(0,100);
                                            valueAnimator.setDuration(3000);
                                            valueAnimator.setInterpolator(new LinearInterpolator());
                                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                @Override
                                                public void onAnimationUpdate(ValueAnimator animation) {
                                                    v = valueAnimator.getAnimatedFraction();
                                                    lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                                                    lat = v * endPosition.latitude + (1 - v) * startPosition.latitude;

                                                    LatLng newPos = new LatLng(lat, lng);
                                                    marker.setPosition(newPos);
                                                    marker.setAnchor(0.5f, 0.5f);
                                                    marker.setRotation(getBearing(startPosition,newPos));
                                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                            .target(newPos)
                                                            .zoom(15.5f)
                                                            .build()
                                                    ));
                                                }
                                            });
                                            valueAnimator.start();
                                            handler.postDelayed(this,3000);
                                        }
                                    }, 3000);*/
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(MapsActivity.this, ""+ t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            }
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }

    }

    private float getBearing(LatLng startPosition, LatLng newPos) {
        double lat = Math.abs(startPosition.latitude - newPos.latitude);
        double lng = Math.abs(startPosition.longitude - newPos.longitude);

        if(startPosition.latitude < newPos.latitude && startPosition.longitude < newPos.longitude){
            return (float) (Math.toDegrees(Math.atan(lng/lat)));
        }
        else if(startPosition.latitude >= newPos.latitude && startPosition.longitude < newPos.longitude){
            return (float) ((90 - Math.toDegrees(Math.atan(lng/lat))) + 90);
        }
        else if(startPosition.latitude >= newPos.latitude && startPosition.longitude >= newPos.longitude){
            return (float) (Math.toDegrees(Math.atan(lng/lat)) + 180);
        }
        else if(startPosition.latitude < newPos.latitude && startPosition.longitude >= newPos.longitude){
            return (float) ((90 - Math.toDegrees(Math.atan(lng/lat))) + 270);
        }
        return -1;
    }

    private List<LatLng> decodePoly(String encoded) {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                this.userLocation = userLocation;
                if(this.userLocation != null){
                    mapFragment.getMapAsync(MapsActivity.this);
                    Log.d("mapsActivity", "onConnected: Obtenidas coordenadas del usuario!");
                }else{
                    googleApiClient.reconnect();
                }

            }else{
                final String[] permission = new String[]{ACCESS_FINE_LOCATION};
                requestPermissions(permission,ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }

        }else{
            Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            this.userLocation = userLocation;
            mapFragment.getMapAsync(MapsActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                googleApiClient.reconnect();
                Log.d("mapsActivity", "onRequestPermissionsResult: Conexión exitosa!");
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Acceder a la ubicación del teléfono");
                    builder.setMessage("Debes aceptar este permiso para poder usar la app");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String[] permission = new String[]{ACCESS_FINE_LOCATION};
                            requestPermissions(permission, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    });
                    builder.show();
                }
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("mapsActivity", "onConnectionSuspended: ");
        googleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("mapsActivity", "onConnectionFailed: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        //marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        Log.d("mapsActivity", "onLocationChanged: Nueva posicion obtenida! ");

        float distancia = location.distanceTo(mDestiny);
        Log.d("mapsActivity", "Distancia: " + distancia);

        if(isFirstPosition){
            startPosition = new LatLng(location.getLatitude(), location.getLongitude());
            marker.setPosition(startPosition);
            mMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(new CameraPosition.Builder()
                                                            .target(startPosition)
                                                            .zoom(15.5f)
                                                            .build()
                    ));
            isFirstPosition = false;
        }else{
            endPosition = new LatLng(location.getLatitude(),location.getLongitude());
            if(startPosition.equals(endPosition)){
                Log.d("mapsActivity", "onLocationChanged: MISMAS COORDENADAS");
            }else{
                Log.d("mapsActivity", "onLocationChanged: COORDENADAS DIFERENTES");
                startAnimation(startPosition, endPosition);
            }

        }

    }

    private void startAnimation(final LatLng start, final LatLng end) {
        Log.d("mapsActivity", "startAnimation: LLAMADA");
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(0,10);
        valueAnimator.setDuration(400);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                v = animation.getAnimatedFraction();
                if(v>=0.0f){
                //Log.d("mapsActivity", "onAnimationUpdate: " + v);
                lng = v * end.longitude + (1 - v) * start.longitude;
                lat = v * end.latitude + (1 - v) * start.latitude;

                LatLng newPos = new LatLng(lat,lng);
                marker.setPosition(newPos);
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(getBearing(start, end) - 90);

                /*mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                                                    .target(newPos)
                                                                                    .zoom(15.5f)
                                                                                    .build()
                ));*/
                startPosition = marker.getPosition();}
            }
        });
        valueAnimator.start();
    }
}
