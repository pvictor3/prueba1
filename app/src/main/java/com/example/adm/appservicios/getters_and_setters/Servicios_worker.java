package com.example.adm.appservicios.getters_and_setters;

/**
 * Created by Adm on 12/03/2018.
 */

public class Servicios_worker {
    private String id;
    private String servicio;
    private String descripcion;
    private String direccion;

    public Servicios_worker() {
    }

    public Servicios_worker(String id, String servicio, String descripcion, String direccion) {
        this.id = id;
        this.servicio = servicio;
        this.descripcion = descripcion;
        this.direccion = direccion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
