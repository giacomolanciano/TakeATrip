package com.example.david.takeatrip.Utilities;

import android.media.Image;
import android.widget.ImageView;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Viaggio;

public class DataObject {
    private Viaggio viaggio;
    private Profilo profilo;
    private ImageView immagineViaggio;



    public DataObject(Viaggio v, Profilo p){
        viaggio = v;
        profilo = p;

        //immagineViaggio = immagine;
    }

    public void setViaggio(Viaggio viaggio) {
        this.viaggio = viaggio;
    }

    public void setImmagineViaggio(ImageView immagineViaggio) {
        this.immagineViaggio = immagineViaggio;
    }

    public Viaggio getViaggio() {

        return viaggio;
    }

    public String getNomeViaggio() {

        return viaggio.getNome();

    }  public String getCodiceViaggio() {

        return viaggio.getCodice();
    }

    public ImageView getImmagineViaggio() {
        return immagineViaggio;
    }


    public Profilo getProfilo() {
        return profilo;
    }

    public String getEmail() {
        return profilo.getEmail();
    }

    public void setProfilo(Profilo profilo) {
        this.profilo = profilo;
    }

}