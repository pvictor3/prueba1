package com.example.adm.appservicios.Fragments;


import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adm.appservicios.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FragmentMapa extends Fragment implements OnMapReadyCallback {

    public static GoogleMap mMap;

    public FragmentMapa() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_mapa, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }
    /*
     LatLng marker = new LatLng(19.33978502, -99.19086277);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
        googleMap.addMarker(new MarkerOptions().title("Marca de Prueba 1").position(marker));
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        setCurretPosition();
    }




    public void setCurretPosition(){
        LatLng marker = new LatLng(19.33978502, -99.19086277 );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
    }

    public static void placeMarker (LatLng point, int pos, String Nombre) {
        if (mMap != null) {
            if (pos == 0){
                mMap.clear();
            }

            MarkerOptions markerOptions = new MarkerOptions();

            // Setting latitude and longitude for the marker
            markerOptions.position(point);
            markerOptions.title(Nombre);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));

            // Adding marker on the Google Map
            mMap.addMarker(markerOptions);
        }
    }


}