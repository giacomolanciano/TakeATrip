package com.example.david.takeatrip.Classes;

/**
 * Created by lucagiacomelli on 08/01/16.
 */
public class Viaggio {


    private String codice, nome, urlImmagine;

    public Viaggio(String codice, String nome) {
        this.codice = codice;
        this.nome = nome;
    }

    public Viaggio(String codice, String nome, String urlImmagine) {
        this.codice = codice;
        this.nome = nome;
        this.urlImmagine = urlImmagine;
    }

    public Viaggio(String codice) {
        this.codice = codice;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlImmagine(){
        return urlImmagine;
    }
}
