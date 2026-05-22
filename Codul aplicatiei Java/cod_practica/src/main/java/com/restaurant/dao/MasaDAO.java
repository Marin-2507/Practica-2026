package com.restaurant.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.restaurant.model.Masa;
import com.restaurant.util.Util;

public class MasaDAO {

    private static final String SELECT =
        "SELECT IdMasa, NumarMasa, Capacitate, Ocupata FROM Masa ";

    public List<Masa> findAll() throws SQLException {
        List<Masa> lista = new ArrayList<>();
        ResultSet rs = Util.query(SELECT + "ORDER BY NumarMasa");
        while (rs.next()) lista.add(map(rs));
        return lista;
    }

    public Masa findById(int id) throws SQLException {
        ResultSet rs = Util.query(SELECT + "WHERE IdMasa=?", id);
        return rs.next() ? map(rs) : null;
    }

    public void insert(Masa m) throws SQLException {
        int id = Util.insert(
            "INSERT INTO Masa (NumarMasa, Capacitate, Ocupata) VALUES (?,?,?)",
            m.getNumarMasa(), m.getCapacitate(), m.isOcupata());
        m.setId(id);
    }

    public void update(Masa m) throws SQLException {
        Util.update(
            "UPDATE Masa SET NumarMasa=?, Capacitate=?, Ocupata=? WHERE IdMasa=?",
            m.getNumarMasa(), m.getCapacitate(), m.isOcupata(), m.getId());
    }

    public void setOcupata(int idMasa, boolean ocupata) throws SQLException {
        Util.update("UPDATE Masa SET Ocupata=? WHERE IdMasa=?", ocupata, idMasa);
    }

    public void delete(int id) throws SQLException {
        Util.update("DELETE FROM Masa WHERE IdMasa=?", id);
    }

    private Masa map(ResultSet rs) throws SQLException {
        return new Masa(rs.getInt("IdMasa"), rs.getInt("NumarMasa"),
                rs.getInt("Capacitate"), rs.getBoolean("Ocupata"));
    }
}