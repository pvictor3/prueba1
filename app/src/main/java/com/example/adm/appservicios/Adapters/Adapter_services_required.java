package com.example.adm.appservicios.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Servicios_worker;

import java.util.List;

/**
 * Created by Adm on 12/03/2018.
 */

public class Adapter_services_required extends RecyclerView.Adapter<Adapter_services_required.ServicesRequiredViewHolder>{

    private List<Servicios_worker> servicios;

    public static class ServicesRequiredViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView tag_servicio;
        public TextView servicio;
        public TextView zona ;
        public TextView money;
        private Context context;


        private ServicesRequiredViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            id              = (TextView) itemView.findViewById(R.id.txtView_card_service_id);
            tag_servicio    = (TextView) itemView.findViewById(R.id.txtView_card_tag_serviceAsked);
            money           = (TextView) itemView.findViewById(R.id.txtView_card_service_money);
            servicio        = (TextView) itemView.findViewById(R.id.txtView_card_serviceAsked);
            zona            = (TextView) itemView.findViewById(R.id.txtView_card_service_zona);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    final int position = getAdapterPosition();
//                    CharSequence profesion = id.getText();
//                    Intent intent = new Intent(itemView.getContext(), Activity_service.class);
//                    intent.putExtra("id", profesion);
//                    context.startActivity(intent);
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
