package com.example.david.takeatrip.Classes;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giacomo on 15/01/2016.
 */
public class Itinerario {

    private Profilo profilo;
    private ArrayList tappe;
    private Date dataInizio, dataFine;
    private Viaggio viaggio;

    public Itinerario(Viaggio viaggio, Profilo profilo, Date dataInizio, Date dataFine) {

        this.viaggio = viaggio;
        this.profilo = profilo;
        this.tappe = new ArrayList();
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    public Viaggio getViaggio() {
        return viaggio;
    }

    public void setViaggio(Viaggio viaggio) {
        this.viaggio = viaggio;
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

        if (!getProfilo().equals(that.getProfilo())) return false;
        return getViaggio().equals(that.getViaggio());

    }

    @Override
    public int hashCode() {
        int result = getProfilo().hashCode();
        result = 31 * result + getViaggio().hashCode();
        return result;
    }
}
