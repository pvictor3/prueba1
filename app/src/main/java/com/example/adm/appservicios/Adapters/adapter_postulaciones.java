package com.example.adm.appservicios.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.workers;

import java.util.List;

/**
 * Created by Adm on 20/03/2018.
 */

public class adapter_postulaciones extends RecyclerView.Adapter<adapter_postulaciones.PostuladosViewHolder>{

    private List<workers> servicios;

    public static class PostuladosViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView nombre;
        public TextView tipo;
        private Context context;


        private PostuladosViewHolder(final View itemView) {
            super(itemView);
            context         = itemView.getContext();
            id              = (TextView) itemView.findViewById(R.id.txtView_card_service_id);
            nombre    = (TextView) itemView.findViewById(R.id.textNombre);
            tipo           = (TextView) itemView.findViewById(R.id.textTipo);

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
                }
            });

        }
    }

    public adapter_postulaciones(List<workers> servicios) {
        this.servicios = servicios;
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
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }
}
