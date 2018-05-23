package com.example.adm.appservicios.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.MensajeChat;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>{
    private ArrayList<MensajeChat> mDataset;
    private String tipoUsuario;

    public ChatRecyclerViewAdapter(ArrayList<MensajeChat> myData, String tipoUsuario){
        mDataset = myData;
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public ChatRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_chat,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.msgTextView.setText(mDataset.get(position).getMensaje());

        if(mDataset.get(position).getEmisor() && tipoUsuario.equals("Usuario")){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)holder.msgTextView.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_END,1);
            lp.addRule(RelativeLayout.ALIGN_PARENT_START,0);
            holder.msgTextView.setLayoutParams(lp);
            holder.msgTextView.setBackground(holder.context.getResources().getDrawable(R.drawable.background_msg_chat_user));
            Log.d("chatActivity", "onBindViewHolder: RIGHT");
        }
        else if(!mDataset.get(position).getEmisor() && !tipoUsuario.equals("Usuario")){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)holder.msgTextView.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
            lp.addRule(RelativeLayout.ALIGN_PARENT_START,0);
            holder.msgTextView.setLayoutParams(lp);
            holder.msgTextView.setBackground(holder.context.getResources().getDrawable(R.drawable.background_msg_chat_user));
            Log.d("chatActivity", "onBindViewHolder: RIGHT");
        }else{
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)holder.msgTextView.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_START,1);
            lp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            holder.msgTextView.setLayoutParams(lp);
            holder.msgTextView.setBackground(holder.context.getResources().getDrawable(R.drawable.background_msg_chat_no_user));
            Log.d("chatActivity", "onBindViewHolder: LEFT");
        }
        Date fecha = new Date(Long.valueOf(mDataset.get(position).getFecha()));
        DateFormat horaFormat = new SimpleDateFormat("HH:mm");
        String hora = horaFormat.format(fecha);
        holder.horaTextView.setText(hora);
        Log.d("chatActivity", "onBindViewHolder: fecha " + hora);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView msgTextView;
        public TextView horaTextView;
        public Context context;
        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            msgTextView = itemView.findViewById(R.id.chat_item_msg);
            horaTextView = itemView.findViewById(R.id.chat_hora);
        }
    }
}
