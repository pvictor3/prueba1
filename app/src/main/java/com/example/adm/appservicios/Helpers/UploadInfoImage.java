package com.example.adm.appservicios.Helpers;

/**
 * Created by Adm on 15/03/2018.
 */

public class UploadInfoImage {

    public String name, url, idservice, idusuario, idcita;

    public UploadInfoImage() {
    }

    public UploadInfoImage(String name, String url, String idservice, String idusuario, String idcita) {
        this.name = name;
        this.url = url;
        this.idservice = idservice;
        this.idusuario = idusuario;
        this.idcita = idcita;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIdservice() {
        return idservice;
    }

    public void setIdservice(String idservice) {
        this.idservice = idservice;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }

    public String getIdcita() {
        return idcita;
    }

    public void setIdcita(String idcita) {
        this.idcita = idcita;
    }
}
