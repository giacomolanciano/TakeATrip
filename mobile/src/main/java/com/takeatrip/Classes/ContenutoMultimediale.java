package com.takeatrip.Classes;

/**
 * Created by lucagiacomelli on 17/03/16.
 */
public class ContenutoMultimediale {
    private String emailProfilo, urlContenuto, livelloCondivisione, codiceViaggio, ordineTappa;

    public ContenutoMultimediale(String urlContenuto, String livelloCondivisione) {
        this.urlContenuto = urlContenuto;
        this.livelloCondivisione = livelloCondivisione;
    }



    public ContenutoMultimediale(String urlContenuto, String codiceViaggio, String livelloCondivisione) {

        this.urlContenuto = urlContenuto;
        this.codiceViaggio = codiceViaggio;
        this.livelloCondivisione = livelloCondivisione;
    }

    public ContenutoMultimediale(String emailProfilo, String urlContenuto, String codiceViaggio, String livelloCondivisione) {
        this.emailProfilo = emailProfilo;
        this.codiceViaggio = codiceViaggio;
        this.urlContenuto = urlContenuto;
        this.livelloCondivisione = livelloCondivisione;
    }


    public String getUrlContenuto() {

        return urlContenuto;
    }


    public String getEmailProfilo() {
        return emailProfilo;
    }

    public void setEmailProfilo(String emailProfilo) {
        this.emailProfilo = emailProfilo;
    }

    public String getLivelloCondivisione() {
        return livelloCondivisione;
    }

    public String getCodiceViaggio() {
        return codiceViaggio;
    }

    public String getOrdineTappa() {
        return ordineTappa;
    }
}
