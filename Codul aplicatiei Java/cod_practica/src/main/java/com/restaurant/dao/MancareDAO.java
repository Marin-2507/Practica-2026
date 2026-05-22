package com.restaurant.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.restaurant.model.Mancare;
import com.restaurant.util.Util;

public class MancareDAO {

    private static final String SELECT =
        "SELECT IdMancare, Denumire, Pret, Categorie, Disponibila FROM Mancare ";

    public List<Mancare> findAll() throws SQLException {
        List<Mancare> lista = new ArrayList<>();
        ResultSet rs = Util.query(SELECT + "ORDER BY Categorie, Denumire");
        while (rs.next()) lista.add(map(rs));
        return lista;
    }

    public Mancare findById(int id) throws SQLException {
        ResultSet rs = Util.query(SELECT + "WHERE IdMancare=?", id);
        return rs.next() ? map(rs) : null;
    }

    public List<Mancare> findDisponibile() throws SQLException {
        List<Mancare> lista = new ArrayList<>();
        ResultSet rs = Util.query(SELECT + "WHERE Disponibila=1 ORDER BY Categorie, Denumire");
        while (rs.next()) lista.add(map(rs));
        return lista;
    }

    public List<String> findCategorii() throws SQLException {
        List<String> lista = new ArrayList<>();
        ResultSet rs = Util.query("SELECT DISTINCT Categorie FROM Mancare ORDER BY Categorie");
        while (rs.next()) lista.add(rs.getString("Categorie"));
        return lista;
    }

    public void insert(Mancare m) throws SQLException {
        int id = Util.insert(
            "INSERT INTO Mancare (Denumire, Pret, Categorie, Disponibila) VALUES (?,?,?,?)",
            m.getDenumire(), m.getPret(), m.getCategorie(), m.isDisponibila());
        m.setId(id);
    }

    public void update(Mancare m) throws SQLException {
        Util.update(
            "UPDATE Mancare SET Denumire=?, Pret=?, Categorie=?, Disponibila=? WHERE IdMancare=?",
            m.getDenumire(), m.getPret(), m.getCategorie(), m.isDisponibila(), m.getId());
    }

    public void delete(int id) throws SQLException {
        Util.update("DELETE FROM Mancare WHERE IdMancare=?", id);
    }

    private Mancare map(ResultSet rs) throws SQLException {
        return new Mancare(rs.getInt("IdMancare"), rs.getString("Denumire"),
                rs.getBigDecimal("Pret"), rs.getString("Categorie"), rs.getBoolean("Disponibila"));
    }
}