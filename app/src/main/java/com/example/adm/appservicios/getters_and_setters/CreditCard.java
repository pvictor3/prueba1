package com.example.adm.appservicios.getters_and_setters;

public class CreditCard {
    private String cardNumber;
    private String expirationDate;
    private String name;
    private String cvc;

    public CreditCard(String cardNumber, String expirationDate, String name, String cvc){
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.name = name;
        this.cvc = cvc;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}
