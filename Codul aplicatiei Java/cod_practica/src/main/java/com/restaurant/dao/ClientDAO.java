package com.restaurant.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.restaurant.model.Client;
import com.restaurant.util.Util;

public class ClientDAO {

    private static final String SELECT =
        "SELECT IdClient, Nume, Telefon, Email FROM Client ";

    public List<Client> findAll() throws SQLException {
        List<Client> lista = new ArrayList<>();
        ResultSet rs = Util.query(SELECT + "ORDER BY Nume");
        while (rs.next()) lista.add(map(rs));
        return lista;
    }

    public List<Client> searchByNume(String termen) throws SQLException {
        List<Client> lista = new ArrayList<>();
        ResultSet rs = Util.query(SELECT + "WHERE Nume LIKE ? ORDER BY Nume", "%" + termen + "%");
        while (rs.next()) lista.add(map(rs));
        return lista;
    }

    public void insert(Client c) throws SQLException {
        int id = Util.insert(
            "INSERT INTO Client (Nume, Telefon, Email) VALUES (?,?,?)",
            c.getNume(), c.getTelefon(), c.getEmail());
        c.setId(id);
    }

    public void update(Client c) throws SQLException {
        Util.update(
            "UPDATE Client SET Nume=?, Telefon=?, Email=? WHERE IdClient=?",
            c.getNume(), c.getTelefon(), c.getEmail(), c.getId());
    }

    public void delete(int id) throws SQLException {
        Util.update("DELETE FROM Client WHERE IdClient=?", id);
    }

    private Client map(ResultSet rs) throws SQLException {
        return new Client(rs.getInt("IdClient"), rs.getString("Nume"),
                rs.getString("Telefon"), rs.getString("Email"));
    }
}