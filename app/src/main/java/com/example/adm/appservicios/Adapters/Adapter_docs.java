package com.example.adm.appservicios.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Doc;

import java.util.ArrayList;

/**
 * Created by Adm on 15/03/2018.
 */

public class Adapter_docs extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private HeaderViewHolder headerHolder;

    private ArrayList<Doc> documentos;
    private Context context;

    private ArrayAdapter<String> arrayAdapter;

    MyClickListener myClickListener;



    public Adapter_docs(Context context, ArrayList<Doc> generics, MyClickListener myClickListener) {
        this.context = context;
        this.documentos = generics;
        this.myClickListener = myClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.card_header, parent, false);
            return new HeaderViewHolder (v);
        } else if(viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.card_foter, parent, false);
            return new FooterViewHolder (v);
        } else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.card_item, parent, false);
            return new GenericViewHolder (v);
        }
        return null;
    }

    private Doc getItem (int position) {
        return documentos.get (position);
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof HeaderViewHolder) {
            headerHolder = (HeaderViewHolder) holder;
            headerHolder.card_view_header.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View view) {
                    createDialogServices();
                }
            });
        } else if(holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;

            /*Click en boton para enviar documentos a servidor*/
            footerHolder.send_Button.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View view) {
                    Toast.makeText (context, "Enviando documentos...", Toast.LENGTH_SHORT).show ();
//                    if(CheckImagesAreSaved.checkImagesRow(context)){
//                        Toast.makeText (context, "Enviando los documentos...", Toast.LENGTH_SHORT).show ();
//                        CharSequence profesion = headerHolder.card_txt_description.getText();
//                        SendDocs.uploadImages(context, profesion);
//                    }else{
//                        Toast.makeText (context, "Faltan fotos por seleccionar", Toast.LENGTH_SHORT).show ();
//                    }
                }
            });
        } else if(holder instanceof GenericViewHolder) {
            final Doc currentItem = getItem (position - 1);
            final GenericViewHolder genericViewHolder = (GenericViewHolder) holder;
            genericViewHolder.txtTitle.setText(currentItem.getTittle());
            genericViewHolder.txtDescription.setText(currentItem.getDescription());
            genericViewHolder.card_view_item.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View view) {
                    String dockey = currentItem.getImgKey();
                    myClickListener.onItemClick(dockey, position);
                }
            });
        }
    }
    //    need to override this method
    @Override
    public int getItemViewType (int position) {
        if(isPositionHeader (position)) {
            return TYPE_HEADER;
        } else if(isPositionFooter (position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader (int position) {
        return position == 0;
    }

    private boolean isPositionFooter (int position) {
        return position == documentos.size () + 1;
    }

    @Override
    public int getItemCount () {
        return documentos.size () + 2;
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        Button send_Button;
        FooterViewHolder(View itemView) {
            super (itemView);
            this.send_Button = (Button) itemView.findViewById (R.id.btn_send_docs);

        }
    }
    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        CardView card_view_header;
        TextView card_txt_title;
        TextView card_txt_description;
        HeaderViewHolder(View itemView) {
            super (itemView);
            this.card_view_header = (CardView) itemView.findViewById (R.id.card_view_header);
            this.card_txt_title = (TextView) itemView.findViewById(R.id.card_header_textView_tittle);
            this.card_txt_description = (TextView) itemView.findViewById(R.id.card_header_textView_description);
        }
    }
    private class GenericViewHolder extends RecyclerView.ViewHolder {
        CardView card_view_item;
        TextView txtTitle;
        TextView txtDescription;
        ImageView imageViewIcon_itemCard;
        GenericViewHolder(View itemView) {
            super(itemView);
            this.card_view_item = (CardView) itemView.findViewById(R.id.card_view_item);
            this.txtTitle = (TextView) itemView.findViewById(R.id.card_item_textView_tittle);
            this.txtDescription = (TextView) itemView.findViewById(R.id.card_item_textView_description);
            this.imageViewIcon_itemCard = (ImageView) itemView.findViewById(R.id.card_item_icon);
        }
    }

    private void onSelectOficio(String string) {
        headerHolder.card_txt_description.setText(string);
    }

    private void createDialogServices() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_work_black);
        builderSingle.setTitle("¿Qué oficio desempeñas?");

        /*Obtener profesion*/
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
//        GetOficiosFromServer.getAllWorks(context, new GetOficiosFromServer.VolleyCallBack() {
//            @Override
//            public void onSuccess(ArrayList<String> oficios) {
//                for (String profesion : oficios) {
//                    arrayAdapter.add(profesion);
//                }
//            }
//            @Override
//            public void onFail(String msg) {
//                //Toast.makeText(context, "Error al recuperar información", LENGTH_LONG).show();
//            }
//        });
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                onSelectOficio(strName);
            }
        });
        builderSingle.show();
    }

    public interface MyClickListener {
        public void onItemClick(String dockey, int position);
    }
}
