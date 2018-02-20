package com.example.adm.appservicios.Helpers;

/**
 * Created by Adm on 20/02/2018.
 */

public class User {

    private String uid, Nombre, Telefono, Contrasena;

    public User() {
    }

    public User(String uid, String nombre, String telefono, String contrasena) {
        this.uid = uid; // Primary Key
        Nombre = nombre;
        Telefono = telefono;
        Contrasena = contrasena;
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
}
