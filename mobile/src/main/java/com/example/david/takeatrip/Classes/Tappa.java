package com.example.david.takeatrip.Classes;

import java.util.Date;

/**
 * Created by Giacomo on 15/01/2016.
 */
public class Tappa {

    private Itinerario itinerario;
    private int ordine;
    private Tappa tappaPrecedente;
    private Date data;
    private String paginaDiario;
    private POI poi;

    public Tappa(Itinerario itinerario, int ordine) {
        this.itinerario = itinerario;
        this.ordine = ordine;
    }

    public Tappa(Itinerario itinerario, int ordine, Tappa tappaPrecedente, Date data, String paginaDiario, POI poi) {
        this.itinerario = itinerario;
        this.ordine = ordine;
        this.tappaPrecedente = tappaPrecedente;
        this.data = data;
        this.paginaDiario = paginaDiario;
        this.poi = poi;
    }

    public Tappa(Itinerario itinerario, int ordine, Date data) {
        this.itinerario = itinerario;
        this.ordine = ordine;
        this.data = data;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public int getOrdine() {
        return ordine;
    }

    public void setOrdine(int ordine) {
        this.ordine = ordine;
    }

    public Tappa getTappaPrecedente() {
        return tappaPrecedente;
    }

    public void setTappaPrecedente(Tappa tappaPrecedente) {
        this.tappaPrecedente = tappaPrecedente;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getPaginaDiario() {
        return paginaDiario;
    }

    public void setPaginaDiario(String paginaDiario) {
        this.paginaDiario = paginaDiario;
    }

    public POI getPoi() {
        return poi;
    }

    public void setPoi(POI poi) {
        this.poi = poi;
    }

    public String getNome() {
        //TODO
        //return this.poi.getNome();
        return "tappa";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tappa tappa = (Tappa) o;

        if (getOrdine() != tappa.getOrdine()) return false;
        return getItinerario().equals(tappa.getItinerario());

    }

    @Override
    public int hashCode() {
        int result = getItinerario().hashCode();
        result = 31 * result + getOrdine();
        return result;
    }

    public String toString(){
        return getPoi().codicePOI + " " +getNome();
    }
}
