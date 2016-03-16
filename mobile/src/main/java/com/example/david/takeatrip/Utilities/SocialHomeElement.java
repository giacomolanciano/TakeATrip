package com.example.david.takeatrip.Utilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Viaggio;

/**
 * Created by Giacomo Lanciano on 16/03/2016.
 */
public class SocialHomeElement implements Parcelable {

    private Viaggio viaggio;
    private Profilo profilo, follow;
    private ImageView immagineViaggio;

    public static final Parcelable.Creator<SocialHomeElement> CREATOR
            = new Parcelable.Creator<SocialHomeElement>() {
        public SocialHomeElement createFromParcel(Parcel in) {
            return new SocialHomeElement(in);
        }

        public SocialHomeElement[] newArray(int size) {
            return new SocialHomeElement[size];
        }
    };

    private SocialHomeElement(Parcel in) {
        //TODO prendere dati da in e popolare campi
    }



    public SocialHomeElement(Viaggio v, Profilo p, ImageView immagine){
        viaggio = v;
        profilo = p;
        immagineViaggio = immagine;
    }

    public SocialHomeElement(Profilo p){
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
