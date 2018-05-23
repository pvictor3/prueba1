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
import com.example.adm.appservicios.Activity.MainActivity;
import com.example.adm.appservicios.Activity.MisServiciosActivity;
import com.example.adm.appservicios.Activity.ViajeActivity;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Servicios_worker;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Adm on 21/03/2018.
 */

public class adapter_serviceswork extends RecyclerView.Adapter<adapter_serviceswork.ServicesWorkViewHolder> {

    private List<Servicios_worker> servicios;


    /*Declaracion inicial para default session user*/
    static SharedPreferences settings;

    public class ServicesWorkViewHolder extends RecyclerView.ViewHolder
    {
        public TextView id;
        public TextView tag_servicio;
        public TextView servicio;
        public TextView zona ;
        public TextView money;
        private Context context;

        private ServicesWorkViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            id = (TextView) itemView.findViewById(R.id.txtView_card_service_id);
            tag_servicio = (TextView) itemView.findViewById(R.id.textTipo);
            money = (TextView) itemView.findViewById(R.id.txtView_card_service_money);
            servicio = (TextView) itemView.findViewById(R.id.textNombre);
            zona = (TextView) itemView.findViewById(R.id.txtView_card_service_zona);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.i("Informacion click", "Click en serviciowork");

                    /*Declaracion de session */
                    /*settings = context.getSharedPreferences("sesion_user", MODE_PRIVATE);
                    String idusuario = settings.getString("Tipousuario","");

                    final CharSequence profesion = id.getText();*/

                    /*Intent to Main, pass parameter id = 2*/
                    /*Intent intent = new Intent(itemView.getContext(), ViajeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("idservice", profesion);
                    intent.putExtra("idusuario", idusuario);
                    context.startActivity(intent);*/

                    /*Prueba llamada a chat*/
                    Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ejemploID", "hbTZYwM732Uga9lpaTpE");
                    context.startActivity(intent);

                }
            });
        }

    }

    public adapter_serviceswork(List servicios){
        this.servicios = servicios;
    }

    @Override
    public ServicesWorkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    /*Importacion de layout card_service_asked*/
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_service_asked, parent, false);
        return new ServicesWorkViewHolder(v);
    }

    @Override
    public void onBindViewHolder(adapter_serviceswork.ServicesWorkViewHolder holder, int position) {
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
