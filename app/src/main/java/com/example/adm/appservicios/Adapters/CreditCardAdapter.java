package com.example.adm.appservicios.Adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder> {

    @Override
    public CreditCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CreditCardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CreditCardViewHolder extends RecyclerView.ViewHolder{

        public CreditCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}
