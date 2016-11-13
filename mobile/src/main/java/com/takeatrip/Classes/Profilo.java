package com.takeatrip.Classes;

import com.takeatrip.Utilities.Constants;

import java.io.Serializable;

/**
 * Created by lucagiacomelli on 08/01/16.
 */
public class Profilo implements Serializable {

    private String id, email, name, surname, password, dataNascita, nazionalita, sesso, username, lavoro, descrizione, tipo;
    private String idImageProfile, getIdImageCover;
    private int codAccount;

    public Profilo(){}

    public Profilo(String id, String name, String surname, String dataNascita, String nazionalita,
                   String sesso, String username, String lavoro, String descrizione, String tipo) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dataNascita = dataNascita;
        this.codAccount = Constants.DEFAULT_COD_ACCOUNT;
        this.nazionalita = nazionalita;
        this.sesso = sesso;
        this.username = username;
        this.lavoro = lavoro;
        this.descrizione = descrizione;
        this.tipo = tipo;
    }

    public Profilo(String id, String email, String name, String surname, String dataNascita, String nazionalita,
                   String sesso, String username, String lavoro, String descrizione, String tipo) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dataNascita = dataNascita;
        this.codAccount = Constants.DEFAULT_COD_ACCOUNT;
        this.nazionalita = nazionalita;
        this.sesso = sesso;
        this.username = username;
        this.lavoro = lavoro;
        this.descrizione = descrizione;
        this.tipo = tipo;
    }

    public Profilo(String id, String name, String surname, String dataNascita,
                   String nazionalita, String sesso, String username, String lavoro,
                   String descrizione, String tipo, String idImageProfile, String idImageCover) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dataNascita = dataNascita;
        this.codAccount = Constants.DEFAULT_COD_ACCOUNT;
        this.nazionalita = nazionalita;
        this.sesso = sesso;
        this.username = username;
        this.lavoro = lavoro;
        this.descrizione = descrizione;
        this.tipo = tipo;
        this.idImageProfile = idImageProfile;
        this.getIdImageCover = idImageCover;
    }


    public Profilo(String id, String email, String name, String surname, String dataNascita,
                   String nazionalita, String sesso, String username, String lavoro,
                   String descrizione, String tipo, String idImageProfile, String idImageCover) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dataNascita = dataNascita;
        this.codAccount = Constants.DEFAULT_COD_ACCOUNT;
        this.nazionalita = nazionalita;
        this.sesso = sesso;
        this.username = username;
        this.lavoro = lavoro;
        this.descrizione = descrizione;
        this.tipo = tipo;
        this.idImageProfile = idImageProfile;
        this.getIdImageCover = idImageCover;
    }


    public Profilo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword(){ if(password!= null)return this.password; else return null;}

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getNazionalita() { return nazionalita; }

    public void setNazionalita(String nazionalita) {
        this.nazionalita = nazionalita;
    }

    public String getSesso() { return sesso; }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getUsername() {return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLavoro() {
        return lavoro;
    }

    public void setLavoro(String lavoro) {
        this.lavoro = lavoro;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo =tipo;
    }

    public String getIdImageProfile() {
        return idImageProfile;
    }

    public String getGetIdImageCover() {
        return getIdImageCover;
    }

    public String toString() {
        return getName() + " " + getSurname() + " " + getId() + " " + getEmail();
    }

}
