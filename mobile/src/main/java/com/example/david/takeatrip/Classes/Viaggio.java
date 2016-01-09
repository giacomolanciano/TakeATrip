package com.example.david.takeatrip.Classes;

/**
 * Created by lucagiacomelli on 08/01/16.
 */
public class Viaggio {


    private String codice, nome;

    public Viaggio(String c, String n){
        codice = c;
        nome = n;
    }


    public String getCodice() {
        return codice;
    }

    public String getNome() {
        return nome;
    }
}
