package com.example.david.takeatrip.Classes;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giacomo on 15/01/2016.
 */
public class Itinerario {

    private String codice;
    private Profilo profilo;
    private ArrayList tappe;
    private Date dataInizio, dataFine;

    public Itinerario(String codice, Profilo profilo, Date dataInizio) {

        this.codice = codice;
        this.profilo = profilo;
        this.tappe = new ArrayList();
        this.dataInizio = dataInizio;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public Profilo getProfilo() {
        return profilo;
    }

    public void setProfilo(Profilo profilo) {
        this.profilo = profilo;
    }

    public List getTappe() {
        return tappe;
    }

    public void inserisciTappa(Tappa tappa, int ordine) {
        tappe.add(ordine, tappa);
    }

    public void eliminaTappa(Tappa tappa) {
        tappe.remove(tappa);
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Itinerario that = (Itinerario) o;

        if (!getCodice().equals(that.getCodice())) return false;
        return getProfilo().equals(that.getProfilo());

    }

    @Override
    public int hashCode() {
        int result = getCodice().hashCode();
        result = 31 * result + getProfilo().hashCode();
        return result;
    }
}
