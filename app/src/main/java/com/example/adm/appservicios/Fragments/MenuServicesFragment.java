package com.example.adm.appservicios.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.adm.appservicios.Activity.MainActivity;
import com.example.adm.appservicios.Activity.MisServiciosActivity;
import com.example.adm.appservicios.Activity.SolicitarServicioActivity;
import com.example.adm.appservicios.R;

public class MenuServicesFragment extends Fragment {

    RelativeLayout card1, card2;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_menu_services, container, false);

        /*Declaracion de session */
        settings = this.getActivity().getSharedPreferences("sesion_user", Context.MODE_PRIVATE);

        card1 = (RelativeLayout) v.findViewById(R.id.bar1);
        card2 = (RelativeLayout) v.findViewById(R.id.bar2);

        if (settings.getString("Tipousuario","") != "Usuario")
        {
            card2.setVisibility(View.GONE);
        }
        else{
            card2.setVisibility(View.VISIBLE);
        }

        card1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("Click", "Card1");
                Intent intent = new Intent(getActivity(), SolicitarServicioActivity.class);
                startActivity(intent);
            }
        });

        card2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("Click", "Card2");
                Intent intent = new Intent(getActivity(), MisServiciosActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
//        Log.i("Vista creada", " Menu Services");

    }
}
