package com.example.david.takeatrip.Classes;

import java.sql.Date;

/**
 * Created by lucagiacomelli on 08/01/16.
 */
public class Profilo {

    private String email, name, surname;
    private Date dataNascita;

    public Profilo(String email, String name, String surname, Date dataNascita) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dataNascita = dataNascita;
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

    public Date getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }
}
