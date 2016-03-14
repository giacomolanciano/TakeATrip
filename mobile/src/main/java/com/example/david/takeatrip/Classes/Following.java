package com.example.david.takeatrip.Classes;

import com.example.david.takeatrip.Utilities.Constants;

/**
 * Created by lucagiacomelli on 08/01/16.
 */
public class Following {

    private Profilo segue, seguito;

    public Profilo getSegue() {
        return segue;
    }

    public void setSegue(Profilo segue) {
        this.segue = segue;
    }

    public Profilo getSeguito() {
        return seguito;
    }

    public void setSeguito(Profilo seguito) {
        this.seguito = seguito;
    }

    public Following(Profilo segue, Profilo seguito) {
        this.segue = segue;
        this.seguito = seguito;
    }
}
