package com.example.adm.appservicios.Helpers;

/**
 * Created by Adm on 21/02/2018.
 */

public class CreditCards {

    private String numero;
    private Integer icon;

    public CreditCards(String numero, Integer icon) {
        this.numero = numero;
        this.icon = icon;
    }


    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

}
