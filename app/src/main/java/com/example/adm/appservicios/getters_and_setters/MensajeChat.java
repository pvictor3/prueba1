package com.example.adm.appservicios.getters_and_setters;

public class MensajeChat {
    private String idusuario;
    private String idservice;
    private String fecha;
    private String mensaje;
    private boolean emisor;

    public MensajeChat(String idusuario, String idservice, String fecha, String mensaje, boolean emisor){
        this.idusuario = idusuario;
        this.idservice = idservice;
        this.fecha = fecha;
        this.mensaje = mensaje;
        this.emisor = emisor;
    }

    public boolean getEmisor() {
        return emisor;
    }

    public void setEmisor(boolean emisor) {
        this.emisor = emisor;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }

    public String getIdservice() {
        return idservice;
    }

    public void setIdservice(String idservice) {
        this.idservice = idservice;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
