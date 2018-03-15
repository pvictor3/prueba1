package com.example.adm.appservicios.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.adm.appservicios.Adapters.Adapter_docs;
import com.example.adm.appservicios.R;
import com.example.adm.appservicios.getters_and_setters.Doc;
import com.mvc.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Archivos extends Fragment {

    private Bitmap bitmap;
    private ImageView data_base_doc1;
    private ImageView data_base_doc2;
    private ImageView data_base_doc3;
    private ProgressDialog progressDialog;
    private Adapter_docs adapter;

    public Archivos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_archivos, container, false);

//        db = new SQLiteHandler(getActivity().getApplicationContext());
//        RecyclerView recyclerView = (RecyclerView) myView.findViewById (R.id.cardList);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager (getActivity());
//        adapter = new Adapter_docs(getActivity(), getDocumentList(), new Adapter_docs.MyClickListener(){
//            @Override
//            public void onItemClick(String dockey, int position){
//                //Toast.makeText(getActivity(), dockey + " " + position, Toast.LENGTH_LONG).show();
//                ImagePicker.pickImage(FragmentArchivos.this, "Select your sss:");
//                //db.updateDocument(String);
//                DocTemp.docKey = dockey;
//            }
//        });
//        recyclerView.setLayoutManager (linearLayoutManager);
//        recyclerView.setAdapter (adapter);

        return myView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        bitmap = ImagePicker.getImageFromResult(getActivity(), requestCode, resultCode, data);

        if (bitmap != null) {
            String path = ImagePicker.getImagePathFromResult(getActivity(), requestCode, resultCode, data);
            Log.i("Informacion path", path);
//            db.updateDocument(DocTemp.docKey, imageToString(bitmap));
        }

        InputStream is = ImagePicker.getInputStreamFromResult(getActivity(), requestCode, resultCode, data);

        if (is != null) {
            //textView.setText("Got input stream!");
            try {
                is.close();
            } catch (IOException ex) {
                // ignore
            }
        } else {
            //textView.setText("Failed to get input stream!");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public ArrayList<Doc> getDocumentList() {
        ArrayList<Doc> documentos = new ArrayList<> ();
//        for (int i = 0; i < db.getDocsCount(); i++) {
//            Doc item = new Doc ();
//            HashMap<String, String> doc = db.getDocsDetails(i);
//            String name = doc.get("nombre");
//            String description = doc.get("description");
//            String dockey = doc.get("dockey");
//            item.setTittle(name);
//            item.setDescription(description);
//            item.setImgKey(dockey);
//            item.setPosition (i);
//            documentos.add(item);
//        }
        return documentos;
    }

    private byte[] imageToString (Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return imgBytes;
    }
}
