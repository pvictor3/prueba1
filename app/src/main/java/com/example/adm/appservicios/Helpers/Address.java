package com.example.adm.appservicios.Helpers;

/**
 * Created by Adm on 12/03/2018.
 */

public class Address {

    private String uid, Titulo, Direccion, Lat, Lng;

    public Address() {
    }

    public Address(String uid, String titulo, String direccion, String lat, String lng) {
        this.uid = uid;
        Titulo = titulo;
        Direccion = direccion;
        Lat = lat;
        Lng = lng;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }
}
