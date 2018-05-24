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
import com.example.adm.appservicios.Activity.MisPostulacionesActivity;
import com.example.adm.appservicios.Activity.MisServiciosActivity;
import com.example.adm.appservicios.Activity.PostularseActivity;
import com.example.adm.appservicios.Activity.SolicitarServicioActivity;
import com.example.adm.appservicios.R;

public class MenuServicesFragment extends Fragment {

    RelativeLayout card1, card2, card3,card4;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    public MenuServicesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_menu_services, container, false);

        /*Declaracion de session */
        settings = this.getActivity().getSharedPreferences("sesion_user", Context.MODE_PRIVATE);

        card1 = (RelativeLayout) v.findViewById(R.id.bar1);
        card2 = (RelativeLayout) v.findViewById(R.id.bar2);
        card3 = (RelativeLayout) v.findViewById(R.id.bar3);
        card4 = v.findViewById(R.id.bar4);

        String tipousu = settings.getString("Tipousuario","");

        /*Validar si el usuario es trabajador o usuario normal*/
        if (tipousu.equals("Usuario"))
        {
            Log.i("es ", " usuario");
            card1.setVisibility(View.VISIBLE);
            card3.setVisibility(View.GONE);
            card4.setVisibility(View.GONE);
        }
        else
        {
            Log.i("es ", " trabajador");
            card1.setVisibility(View.GONE);
            card2.setVisibility(View.VISIBLE);
            card3.setVisibility(View.VISIBLE);
            card4.setVisibility(View.VISIBLE);
        }

        /*click en card1*/
        card1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("Click", "Card1");
                Intent intent = new Intent(getActivity(), SolicitarServicioActivity.class);
                startActivity(intent);
            }
        });

        /*click en card2*/
        card2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MisServiciosActivity.class);
                startActivity(intent);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MisPostulacionesActivity.class);
                startActivity(intent);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Redireccionar a vista para postularse*/
                Intent intent = new Intent(getActivity(), PostularseActivity.class);
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
