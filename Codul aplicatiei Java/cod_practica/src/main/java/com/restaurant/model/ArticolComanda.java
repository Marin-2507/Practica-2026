package com.restaurant.model;

import java.math.BigDecimal;

public class ArticolComanda {
    private int idArticol;
    private int idComanda;
    private int idMancare;
    private String denumireMancare;
    private int cantitate;
    private BigDecimal pretUnitar;

    public ArticolComanda() {}

    public ArticolComanda(int idArticol, int idComanda, int idMancare,
                          String denumireMancare, int cantitate, BigDecimal pretUnitar) {
        this.idArticol = idArticol;
        this.idComanda = idComanda;
        this.idMancare = idMancare;
        this.denumireMancare = denumireMancare;
        this.cantitate = cantitate;
        this.pretUnitar = pretUnitar;
    }

    public BigDecimal getSubtotal() {
        return pretUnitar.multiply(BigDecimal.valueOf(cantitate));
    }

    public int getIdArticol() { return idArticol; }
    public void setIdArticol(int v) { this.idArticol = v; }

    public int getIdComanda() { return idComanda; }
    public void setIdComanda(int v) { this.idComanda = v; }

    public int getIdMancare() { return idMancare; }
    public void setIdMancare(int v) { this.idMancare = v; }

    public String getDenumireMancare() { return denumireMancare; }
    public void setDenumireMancare(String v) { this.denumireMancare = v; }

    public int getCantitate() { return cantitate; }
    public void setCantitate(int v) { this.cantitate = v; }

    public BigDecimal getPretUnitar() { return pretUnitar; }
    public void setPretUnitar(BigDecimal v) { this.pretUnitar = v; }
}