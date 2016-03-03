package com.example.david.takeatrip.Classes;

import com.example.david.takeatrip.Utilities.Constants;

/**
 * Created by lucagiacomelli on 08/01/16.
 */
public class Profilo {

    private String email, name, surname, password;
    private String dataNascita;
    private int codAccount;

    public Profilo(){
    }

    public Profilo(String email, String name, String surname, String dataNascita) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dataNascita = dataNascita;
        this.codAccount = Constants.DEFAULT_COD_ACCOUNT;
    }


    public Profilo(String email) {
        this.email = email;
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

    public int getCodAccount() {
        return codAccount;
    }

    public void setCodAccount(int codAccount) {
        this.codAccount = codAccount;
    }

    public String toString() {
        return getName() + " " + getSurname() + " " + getEmail();
    }

}
