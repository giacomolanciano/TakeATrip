package com.takeatrip.Classes;

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
    private String nome;
    private String livelloCondivisione;

    public Tappa(Itinerario itinerario, int ordine) {
        this.itinerario = itinerario;
        this.ordine = ordine;
    }

    public Tappa(Itinerario itinerario, int ordine, Tappa tappaPrecedente, Date data, String nomeTappa, POI poi, String livelloCondivisione) {
        this.itinerario = itinerario;
        this.ordine = ordine;
        this.tappaPrecedente = tappaPrecedente;
        this.data = data;
        this.nome = nomeTappa;
        this.poi = poi;
        this.livelloCondivisione = livelloCondivisione;
    }

    public Tappa(Itinerario itinerario, int ordine, Date data, String livelloCondivisione) {
        this.itinerario = itinerario;
        this.ordine = ordine;
        this.data = data;
        this.livelloCondivisione = livelloCondivisione;
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

    public String getName(){
        return nome;
    }

    public void setName(String name){
        this.nome = name;
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


    public String getLivelloCondivisione() {
        return livelloCondivisione;
    }

    public void setLivelloCondivisione(String livelloCondivisione) {
        this.livelloCondivisione = livelloCondivisione;
    }

    public String toString(){
        return getPoi() + " " +getName() +" "+ livelloCondivisione;
    }
}
