package com.example.adm.appservicios.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adm.appservicios.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class IndexFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        Log.i("Vista creada", " index");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("Vista creada", " onMapReady");
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        setCurretPosition();
    }

    public void setCurretPosition(){
        Log.i("Vista creada", " setCurretPosition");
        LatLng marker = new LatLng(19.33978502, -99.19086277 );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
    }

    public void placeMarker (LatLng point, int pos, String Nombre) {
        Log.i("Vista creada", " placeMarker");
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
