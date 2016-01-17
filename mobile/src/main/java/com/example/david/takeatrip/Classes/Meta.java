package com.example.david.takeatrip.Classes;

/**
 * Created by Giacomo on 17/01/2016.
 */
public class Meta {

    private String codiceMeta, placeID, nome, altitudine, temperatura, etaMedia,
            pressione, umidita, temperaturaMin, temperaturaMax, velocitaVento;

    public Meta(String codiceMeta, String placeID, String nome, String altitudine, String temperatura,
                String etaMedia, String pressione, String umidita, String temperaturaMin,
                String temperaturaMax, String velocitaVento) {
        this.codiceMeta = codiceMeta;
        this.placeID = placeID;
        this.nome = nome;
        this.altitudine = altitudine;
        this.temperatura = temperatura;
        this.etaMedia = etaMedia;
        this.pressione = pressione;
        this.umidita = umidita;
        this.temperaturaMin = temperaturaMin;
        this.temperaturaMax = temperaturaMax;
        this.velocitaVento = velocitaVento;
    }

    public Meta(String codiceMeta) {
        this.codiceMeta = codiceMeta;
    }

    public Meta(String codiceMeta, String nome) {
        this.codiceMeta = codiceMeta;
        this.nome = nome;
    }



    public String getCodiceMeta() {
        return codiceMeta;
    }

    public void setCodiceMeta(String codiceMeta) {
        this.codiceMeta = codiceMeta;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAltitudine() {
        return altitudine;
    }

    public void setAltitudine(String altitudine) {
        this.altitudine = altitudine;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getEtaMedia() {
        return etaMedia;
    }

    public void setEtaMedia(String etaMedia) {
        this.etaMedia = etaMedia;
    }

    public String getPressione() {
        return pressione;
    }

    public void setPressione(String pressione) {
        this.pressione = pressione;
    }

    public String getUmidita() {
        return umidita;
    }

    public void setUmidita(String umidita) {
        this.umidita = umidita;
    }

    public String getTemperaturaMin() {
        return temperaturaMin;
    }

    public void setTemperaturaMin(String temperaturaMin) {
        this.temperaturaMin = temperaturaMin;
    }

    public String getTemperaturaMax() {
        return temperaturaMax;
    }

    public void setTemperaturaMax(String temperaturaMax) {
        this.temperaturaMax = temperaturaMax;
    }

    public String getVelocitaVento() {
        return velocitaVento;
    }

    public void setVelocitaVento(String velocitaVento) {
        this.velocitaVento = velocitaVento;
    }
}
