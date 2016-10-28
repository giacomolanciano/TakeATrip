package com.takeatrip.Classes;

/**
 * Created by lucagiacomelli on 28/10/16.
 */

public class NotaTappa {

    private String emailProfilo;
    private int ordineTappa;
    private String livelloCondivisione,nota;

    public void setEmailProfilo(String emailProfilo) {
        this.emailProfilo = emailProfilo;
    }

    public void setOrdineTappa(int ordineTappa) {
        this.ordineTappa = ordineTappa;
    }

    public void setLivelloCondivisione(String livelloCondivisione) {
        this.livelloCondivisione = livelloCondivisione;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getEmailProfilo() {

        return emailProfilo;
    }

    public int getOrdineTappa() {
        return ordineTappa;
    }

    public String getLivelloCondivisione() {
        return livelloCondivisione;
    }

    public String getNota() {
        return nota;
    }

    public NotaTappa(String emailProfilo, int ordineTappa, String livelloCondivisione, String nota) {

        this.emailProfilo = emailProfilo;
        this.ordineTappa = ordineTappa;
        this.livelloCondivisione = livelloCondivisione;
        this.nota = nota;
    }

    public String toString(){
        return emailProfilo +" "+ ordineTappa +" "+livelloCondivisione+" "+ nota;
    }
}
