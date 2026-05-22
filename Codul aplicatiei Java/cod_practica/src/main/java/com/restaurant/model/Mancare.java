package com.restaurant.model;

import java.math.BigDecimal;

public class Mancare extends BaseEntity {
    private String denumire;
    private BigDecimal pret;
    private String categorie;
    private boolean disponibila;

    public Mancare() {}

    public Mancare(int id, String denumire, BigDecimal pret, String categorie, boolean disponibila) {
        super(id);
        this.denumire = denumire;
        this.pret = pret;
        this.categorie = categorie;
        this.disponibila = disponibila;
    }

    public String getDenumire() { return denumire; }
    public void setDenumire(String denumire) { this.denumire = denumire; }

    public BigDecimal getPret() { return pret; }
    public void setPret(BigDecimal pret) { this.pret = pret; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public boolean isDisponibila() { return disponibila; }
    public void setDisponibila(boolean disponibila) { this.disponibila = disponibila; }

    @Override
    public String toDisplayString() {
        return "[" + id + "] " + denumire + " - " + pret + " MDL";
    }

    @Override
    public String toString() { return denumire; }
}