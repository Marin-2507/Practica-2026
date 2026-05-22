package com.restaurant.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.restaurant.enums.OrderStatus;

public class Comanda extends BaseEntity {
    private int idMasa;
    private int numarMasa;
    private int idClient;
    private String numeClient;
    private OrderStatus status;
    private LocalDateTime dataOra;
    private List<ArticolComanda> articole = new ArrayList<>();

    public Comanda() {
        this.status = OrderStatus.NOUA;
        this.dataOra = LocalDateTime.now();
    }

    public Comanda(int id, int idMasa, int numarMasa, int idClient,
                   String numeClient, OrderStatus status, LocalDateTime dataOra) {
        super(id);
        this.idMasa = idMasa;
        this.numarMasa = numarMasa;
        this.idClient = idClient;
        this.numeClient = numeClient;
        this.status = status;
        this.dataOra = dataOra;
        this.articole = new ArrayList<>();
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ArticolComanda a : articole) total = total.add(a.getSubtotal());
        return total;
    }

    public int getIdMasa() { return idMasa; }
    public void setIdMasa(int v) { this.idMasa = v; }

    public int getNumarMasa() { return numarMasa; }
    public void setNumarMasa(int v) { this.numarMasa = v; }

    public int getIdClient() { return idClient; }
    public void setIdClient(int v) { this.idClient = v; }

    public String getNumeClient() { return numeClient; }
    public void setNumeClient(String v) { this.numeClient = v; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus v) { this.status = v; }

    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime v) { this.dataOra = v; }

    public List<ArticolComanda> getArticole() { return articole; }
    public void setArticole(List<ArticolComanda> v) { this.articole = v; }

    @Override
    public String toDisplayString() {
        return "#" + id + " Masa " + numarMasa + " - " + numeClient + " - " + status;
    }

    @Override
    public String toString() { return toDisplayString(); }
}