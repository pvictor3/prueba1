package com.example.adm.appservicios.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adm.appservicios.Activity.PostulacionesActivity;
import com.example.adm.appservicios.Activity.PostularseActivity;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Servicios_worker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Adm on 12/03/2018.
 */

public class Adapter_services_required extends RecyclerView.Adapter<Adapter_services_required.ServicesRequiredViewHolder>{

    private List<Servicios_worker> servicios;

    /*Declaracion inicial para default session user*/
    static SharedPreferences settings;

    public class ServicesRequiredViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView tag_servicio;
        public TextView servicio;
        public TextView zona ;
        public TextView money;
        private Context context;

        /*Firebase*/
        FirebaseFirestore db;
        StorageReference storageReference;
        FirebaseStorage storage;

        private ServicesRequiredViewHolder(final View itemView) {
            super(itemView);

            context         = itemView.getContext();
            id              = (TextView) itemView.findViewById(R.id.txtView_card_service_id);
            tag_servicio    = (TextView) itemView.findViewById(R.id.textTipo);
            money           = (TextView) itemView.findViewById(R.id.txtView_card_service_money);
            servicio        = (TextView) itemView.findViewById(R.id.textNombre);
            zona            = (TextView) itemView.findViewById(R.id.txtView_card_service_zona);

            FirebaseApp.initializeApp(context);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Informacion click", "Click en servicio");

                    /*Declaracion de session */
                    settings = context.getSharedPreferences("sesion_user", MODE_PRIVATE);
                    String tipouser = settings.getString("Tipousuario","");

                    final CharSequence profesion = id.getText();

                    /*Validar usuario para tipo de consulta a base de datos*/
                    if (tipouser.equals("Usuario"))
                    {
                        final int position = getAdapterPosition();
//                    Log.i("Informacion click", String.valueOf(servicios.get(position).getId()));

                        Log.i("Informacion click", String.valueOf(profesion));
                        Intent intent = new Intent(itemView.getContext(), PostulacionesActivity.class);
                        intent.putExtra("id", profesion);
                        context.startActivity(intent);
                    }
                    else
                    {
                        /*Mostrar mensaje de exito*/
//                        Toast.makeText (itemView.getContext(), "Postularse a ." + profesion, Toast.LENGTH_SHORT).show ();

                        db                  = FirebaseFirestore.getInstance();
                        storage             = FirebaseStorage.getInstance();
                        storageReference    = storage.getReference();

                        /*Consultar si ya se habia postulado a este servicio*/
                        db.collection("postulaciones_service")
                                .whereEqualTo("idusu_trab", settings.getString("UIDusuario",""))
                                .whereEqualTo("service", profesion)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                        //Log.i("sucess", String.valueOf(value.size()));

                                        /*Validar si hay registro*/
                                        if (value.size() > 0) {
                                            /*Mostrar alert de confirmacion para postularse*/
                                            AlertDialog.Builder builder;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                                            } else {
                                                builder = new AlertDialog.Builder(context);
                                            }
                                            builder.setMessage("Ya te has postulado a este servicio.")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .show();

                                        } else {
                                            /*Mostrar alert de confirmacion para postularse*/
                                            AlertDialog.Builder builder;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                                            } else {
                                                builder = new AlertDialog.Builder(context);
                                            }
                                            builder.setMessage("¿Postularse?")
                                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Guardar en base de datoa postulaciones_service
                                                            Map<String, Object> postulacion = new HashMap<>();
                                                            postulacion.put("service", profesion);
                                                            postulacion.put("idusu_trab", settings.getString("UIDusuario",""));

                                                            // Agregar postulacion a Base de Datos
                                                            db.collection("postulaciones_service")
                                                                    .add(postulacion)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            Log.i("Informacion", "Servicio agregado a base de datos" + documentReference.getId());

                                                                            /*Mostrar mensaje de success*/
                                                                            Toast.makeText (itemView.getContext(), "Postulacion exitosa.", Toast.LENGTH_SHORT).show ();

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.i("informacion", "Error adding document", e);

                                                                            /*Mostrar mensaje de error*/
                                                                            Toast.makeText (itemView.getContext(), "Hubo un error al postularse, intente de nuevo.", Toast.LENGTH_SHORT).show ();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // do nothing
                                                        }
                                                    })
                                                    .show();

                                        }

                                    }
                                });

                    }

                }
            });

        }
    }

    public Adapter_services_required(List servicios){
        this.servicios = servicios;
    }


    @Override
    public ServicesRequiredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*Importacion de layout card_service_asked*/
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_service_asked, parent, false);
        return new ServicesRequiredViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ServicesRequiredViewHolder holder, int position) {
        Log.i("Dato llega ", servicios.get(position).getDireccion());

        holder.id.setText(servicios.get(position).getId());
        holder.tag_servicio.setText(servicios.get(position).getDescripcion());
        holder.servicio.setText(servicios.get(position).getServicio());
        holder.money.setText(servicios.get(position).getMin());
        holder.zona.setText(servicios.get(position).getDireccion());
    }


    @Override
    public int getItemCount() {
        return servicios.size();
    }
}
