package com.example.david.takeatrip.Classes;

/**
 * Created by lucagiacomelli on 08/01/16.
 */
public class Profilo {

    private String email, name, surname;

    public Profilo(String e, String n, String s){

        email = e;
        name = n;
        surname = s;

    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
