package com.example.adm.appservicios.getters_and_setters;

/**
 * Created by Adm on 20/03/2018.
 */

public class workers {

    private String id;
    private String Nombre;
    private String Tipo_user;

    public workers() {
    }

    public workers(String id, String nombre, String tipo_user) {
        this.id = id;
        Nombre = nombre;
        Tipo_user = tipo_user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTipo_user() {
        return Tipo_user;
    }

    public void setTipo_user(String tipo_user) {
        Tipo_user = tipo_user;
    }
}
