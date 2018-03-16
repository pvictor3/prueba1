package com.example.adm.appservicios.Helpers;

/**
 * Created by Adm on 16/03/2018.
 */

public class UploadCitaImage {

    public String name, url, idusuario, idcita, idimage;

    public UploadCitaImage() {
    }

    public UploadCitaImage(String name, String url, String idusuario, String idcita, String idimage) {
        this.name = name;
        this.url = url;
        this.idusuario = idusuario;
        this.idcita = idcita;
        this.idimage = idimage;
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

    public String getIdimage() {
        return idimage;
    }

    public void setIdimage(String idimage) {
        this.idimage = idimage;
    }
}
