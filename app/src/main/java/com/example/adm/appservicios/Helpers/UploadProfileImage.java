package com.example.adm.appservicios.Helpers;

/**
 * Created by Adm on 17/03/2018.
 */

public class UploadProfileImage {

    public String name, url, idusuario;

    public UploadProfileImage() {
    }

    public UploadProfileImage(String name, String url, String idusuario) {
        this.name = name;
        this.url = url;
        this.idusuario = idusuario;
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
}
