package com.example.adm.appservicios.Helpers;

/**
 * Created by Adm on 20/02/2018.
 */

public class User {

    private String uid, Nombre, Telefono, Contrasena, Tipo;

    public User() {
    }

    public User(String uid, String nombre, String telefono, String contrasena, String tipo) {
        this.uid = uid;
        this.Nombre = nombre;
        this.Telefono = telefono;
        this.Contrasena = contrasena;
        this.Tipo = tipo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getContrasena() {
        return Contrasena;
    }

    public void setContrasena(String contrasena) {
        Contrasena = contrasena;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }
}
