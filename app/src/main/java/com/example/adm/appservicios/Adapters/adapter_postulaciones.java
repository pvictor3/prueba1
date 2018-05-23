package com.example.adm.appservicios.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adm.appservicios.Activity.ChatActivity;
import com.example.adm.appservicios.Activity.PostulacionesActivity;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.workers;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Adm on 20/03/2018.
 */

public class adapter_postulaciones extends RecyclerView.Adapter<adapter_postulaciones.PostuladosViewHolder>{

    private List<workers> servicios;
    private String idServices; /*Para poder regresar de ChatActivity*/


    public static class PostuladosViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView nombre;
        public TextView tipo;
        private Context context;
        /*idServicio para regresar*/
        public String idServ;

        private PostuladosViewHolder(final View itemView) {
            super(itemView);
            context         = itemView.getContext();
            id              = (TextView) itemView.findViewById(R.id.txtView_card_service_id);
            nombre          = (TextView) itemView.findViewById(R.id.textNombre);
            tipo            = (TextView) itemView.findViewById(R.id.textTipo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Informacion click", "Click en postulado");

                    final int position = getAdapterPosition();
//                    Log.i("Informacion click", String.valueOf(servicios.get(position).getId()));
                    CharSequence profesion = id.getText();
                    Log.i("Informacion postula id", String.valueOf(profesion));
//                    Intent intent = new Intent(itemView.getContext(), PostulacionesActivity.class);
//                    intent.putExtra("id", profesion);
//                    context.startActivity(intent);

                    /*Se abre ventana de chat*/
                    Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
                    intent.putExtra("id", profesion);
                    intent.putExtra("idServ",idServ);
                    context.startActivity(intent);
                }
            });

        }
    }

    public adapter_postulaciones(List<workers> servicios, String idServices) { /*Parametro id de servicios*/
        this.servicios = servicios;
        this.idServices = idServices;
    }

    @Override
    public PostuladosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*Importacion de layout card_service_asked*/
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_postulate, parent, false);
        return new PostuladosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(adapter_postulaciones.PostuladosViewHolder holder, int position)
    {
        Log.i("Dato llega ", servicios.get(position).getNombre());

        holder.id.setText(servicios.get(position).getId());
        holder.nombre.setText(servicios.get(position).getNombre());
        holder.tipo.setText(servicios.get(position).getTipo_user());
        holder.idServ = this.idServices;
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }
}
