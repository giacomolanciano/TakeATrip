package com.example.david.takeatrip.Utilities;

import android.widget.ImageView;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Viaggio;

public class DataObject {

    private static final String TAG = "TEST DataObject";

    private Viaggio viaggio;
    private Profilo profilo, follow;
    private ImageView immagineViaggio;



    public DataObject(Viaggio v, Profilo p, ImageView immagine){
        viaggio = v;
        profilo = p;
        immagineViaggio = immagine;
    }

    public DataObject(Profilo p){
        follow = p;
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

    }
    public String getCodiceViaggio() {

        return viaggio.getCodice();
    }

    public String getUrlImageTravel() {

        return viaggio.getUrlImmagine();
    }



    public String getNomeFollow() {

        return follow.getName();
    }
    public String getCognomeFollow() {

        return follow.getSurname();
    }

    public String getUsernameFollow() {

        return follow.getUsername();
    }
    public String getEmailFollow() {

        return follow.getEmail();
    }
    public String getSessoFollow() {

        return follow.getSesso();
    }
    public String getNazionalitaFollow() {

        return follow.getNazionalita();
    }

    public String getLavoroFollow() {

        return follow.getLavoro();
    }
    public String getDataNascitaFollow() {

        return follow.getDataNascita();
    }
    public String getDescrizioneFollow() {

        return follow.getDescrizione();
    }

public String getTipoFollow() {

        return follow.getTipo();
    }


    public String getUrlImmagineProfilo(){
        return follow.getIdImageProfile();
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