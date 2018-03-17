package com.example.adm.appservicios.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.adm.appservicios.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class Citas extends Fragment {

    TextView txtDate, txtHour;
    Button btnUpdate;

    /*Declaracion inicial para default session user*/
    SharedPreferences settings;

    /*Firebase*/
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseStorage storage;

    public Citas() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFirebase();

        /*Declaracion de session */
        settings = this.getActivity().getSharedPreferences("sesion_user", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_citas, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtDate = getView().findViewById(R.id.txt_view_date);
        txtHour = getView().findViewById(R.id.txt_view_hour);

        btnUpdate = getView().findViewById(R.id.button_update_date);

        updateDates();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Update fecha de cita*/
                updateDates();

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

    public void updateDates()
    {
        /*Se consulta si tiene citas el usuario logueado*/
        db.collection("citas")
                .whereEqualTo("idusuario", settings.getString("UIDusuario",""))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                    /*Validar si hay registro*/
                        if (value.size() > 0) {

                            /*Recorrido de datos*/
                            for (DocumentSnapshot doc : value)
                            {
                                Log.i("Fecha", doc.getString("Fecha"));

                                if (doc.getString("Fecha") != "")
                                {
                                    /*Agregar a array para mostrar alertdialog*/
                                    txtDate.setText(doc.getString("Fecha"));
                                }
                                else
                                {
                                    txtDate.setText("Fecha");
                                }

                                if (doc.getString("Hora") != "")
                                {
                                    /*Agregar a array para mostrar alertdialog*/
                                    txtHour.setText(doc.getString("Hora"));
                                }
                                else
                                {
                                    txtHour.setText("Hora");
                                }

                            }

                        } else {
                            Log.i("Error", "No existen citas para el usuario logueado");
                            txtDate.setText("Fecha");
                            txtHour.setText("Hora");
                        }

                    }
                });
    }
}
