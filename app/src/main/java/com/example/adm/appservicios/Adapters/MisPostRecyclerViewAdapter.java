package com.example.adm.appservicios.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adm.appservicios.Activity.ChatActivity;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Servicios_worker;

import java.util.List;

public class MisPostRecyclerViewAdapter extends RecyclerView.Adapter<MisPostRecyclerViewAdapter.ViewHolder> {
    private List<Servicios_worker> servicios;

    public MisPostRecyclerViewAdapter(List<Servicios_worker> servicios){
        this.servicios = servicios;
    }

    @Override
    public MisPostRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*Importacion de layout card_service_asked*/
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_service_asked, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MisPostRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.id.setText(servicios.get(position).getId());
        holder.tag_servicio.setText(servicios.get(position).getServicio());
        holder.servicio.setText(servicios.get(position).getDescripcion());
        holder.money.setText(servicios.get(position).getMin());
        holder.zona.setText(servicios.get(position).getDireccion());
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView tag_servicio;
        public TextView servicio;
        public TextView zona ;
        public TextView money;
        private Context context;

            private ViewHolder(final View itemView){
                super(itemView);
                context         = itemView.getContext();
                id              = (TextView) itemView.findViewById(R.id.txtView_card_service_id);
                tag_servicio    = (TextView) itemView.findViewById(R.id.textTipo);
                money           = (TextView) itemView.findViewById(R.id.txtView_card_service_money);
                servicio        = (TextView) itemView.findViewById(R.id.textNombre);
                zona            = (TextView) itemView.findViewById(R.id.txtView_card_service_zona);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Prueba llamada a chat*/
                        Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ejemploID",id.getText().toString());
                        context.startActivity(intent);
                    }
                });
            }
    }
}
