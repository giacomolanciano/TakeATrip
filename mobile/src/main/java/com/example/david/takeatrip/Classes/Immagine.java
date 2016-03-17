package com.example.david.takeatrip.Classes;

/**
 * Created by lucagiacomelli on 17/03/16.
 */
public class Immagine {
    private String urlImmagine, livelloCondivisione, codiceViaggio, ordineTappa;

    public Immagine(String urlImmagine, String livelloCondivisione) {
        this.urlImmagine = urlImmagine;
        this.livelloCondivisione = livelloCondivisione;
    }

    public Immagine(String urlImmagine, String codiceViaggio, String livelloCondivisione) {

        this.urlImmagine = urlImmagine;
        this.codiceViaggio = codiceViaggio;
        this.livelloCondivisione = livelloCondivisione;
    }

    public String getUrlImmagine() {

        return urlImmagine;
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
