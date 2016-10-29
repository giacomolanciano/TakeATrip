package com.takeatrip.Classes;

/**
 * Created by lucagiacomelli on 28/10/16.
 */

public class NotaTappa {

    private String emailProfilo, username, codiceViaggio;
    private int ordineTappa;
    private String livelloCondivisione,nota;


    public NotaTappa(String emailProfilo, String username, String codiceViaggio, int ordineTappa, String livelloCondivisione, String nota) {

        this.emailProfilo = emailProfilo;
        this.username = username;
        this.codiceViaggio = codiceViaggio;
        this.ordineTappa = ordineTappa;
        this.livelloCondivisione = livelloCondivisione;
        this.nota = nota;
    }

    public void setEmailProfilo(String emailProfilo) {
        this.emailProfilo = emailProfilo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
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

    public String getCodiceViaggio() {
        return codiceViaggio;
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



    public String toString(){
        return emailProfilo +" "+username+" "+codiceViaggio+" "+ ordineTappa +" "+livelloCondivisione+" "+ nota;
    }
}
