package com.takeatrip.Classes;

/**
 * Created by Giacomo on 15/01/2016.
 */
public class POI {

    String codicePOI, fonte;

    public POI(String codicePOI, String fonte) {
        this.codicePOI = codicePOI;
        this.fonte = fonte;
    }

    public String getCodicePOI() {
        return codicePOI;
    }

    public void setCodicePOI(String codicePOI) {
        this.codicePOI = codicePOI;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        POI poi = (POI) o;

        if (!getCodicePOI().equals(poi.getCodicePOI())) return false;
        return getFonte().equals(poi.getFonte());

    }

    @Override
    public int hashCode() {
        int result = getCodicePOI().hashCode();
        result = 31 * result + getFonte().hashCode();
        return result;
    }
}
