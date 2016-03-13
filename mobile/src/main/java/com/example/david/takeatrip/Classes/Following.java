package com.example.david.takeatrip.Classes;

import com.example.david.takeatrip.Utilities.Constants;

/**
 * Created by lucagiacomelli on 08/01/16.
 */
public class Following {

    private String segue, seguito;

    public String getSegue() {
        return segue;
    }

    public void setSegue(String segue) {
        this.segue = segue;
    }

    public String getSeguito() {
        return seguito;
    }

    public void setSeguito(String seguito) {
        this.seguito = seguito;
    }

    public Following(String segue) {

        this.segue = segue;
        this.seguito = seguito;
    }
}
