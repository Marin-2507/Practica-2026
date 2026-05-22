package com.restaurant.model;

public class Masa extends BaseEntity {
    private int numarMasa;
    private int capacitate;
    private boolean ocupata;

    public Masa() {}

    public Masa(int id, int numarMasa, int capacitate, boolean ocupata) {
        super(id);
        this.numarMasa = numarMasa;
        this.capacitate = capacitate;
        this.ocupata = ocupata;
    }

    public int getNumarMasa() { return numarMasa; }
    public void setNumarMasa(int numarMasa) { this.numarMasa = numarMasa; }

    public int getCapacitate() { return capacitate; }
    public void setCapacitate(int capacitate) { this.capacitate = capacitate; }

    public boolean isOcupata() { return ocupata; }
    public void setOcupata(boolean ocupata) { this.ocupata = ocupata; }

    @Override
    public String toDisplayString() {
        return "Masa #" + numarMasa + " (cap. " + capacitate + ") - " + (ocupata ? "Ocupata" : "Libera");
    }

    @Override
    public String toString() { return "Masa #" + numarMasa; }
}