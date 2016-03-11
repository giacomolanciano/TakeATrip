package com.example.david.takeatrip.Utilities;

import android.media.Image;
import android.widget.ImageView;

import com.example.david.takeatrip.Classes.Viaggio;

public class DataObject {
    private Viaggio viaggio;
    private ImageView immagineViaggio;

    public DataObject(Viaggio v){
        viaggio = v;
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
}