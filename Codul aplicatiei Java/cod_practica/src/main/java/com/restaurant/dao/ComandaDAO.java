package com.restaurant.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.restaurant.enums.OrderStatus;
import com.restaurant.model.ArticolComanda;
import com.restaurant.model.Comanda;
import com.restaurant.util.Util;

public class ComandaDAO {

    private static final String SELECT =
        "SELECT c.IdComanda, c.IdMasa, m.NumarMasa, c.IdClient, cl.Nume, c.Status, c.DataOra " +
        "FROM Comanda c " +
        "JOIN Masa m ON m.IdMasa=c.IdMasa " +
        "JOIN Client cl ON cl.IdClient=c.IdClient ";

    public List<Comanda> findAll() throws SQLException {
        List<Comanda> lista = new ArrayList<>();
        ResultSet rs = Util.query(SELECT + "ORDER BY c.DataOra DESC");
        while (rs.next()) { Comanda c = map(rs); c.setArticole(findArticole(c.getId())); lista.add(c); }
        return lista;
    }

    public List<Comanda> findByStatus(OrderStatus status) throws SQLException {
        List<Comanda> lista = new ArrayList<>();
        ResultSet rs = Util.query(SELECT + "WHERE c.Status=? ORDER BY c.DataOra DESC", status.getValue());
        while (rs.next()) { Comanda c = map(rs); c.setArticole(findArticole(c.getId())); lista.add(c); }
        return lista;
    }

    public List<Comanda> findByMasa(int idMasa) throws SQLException {
        List<Comanda> lista = new ArrayList<>();
        ResultSet rs = Util.query(SELECT + "WHERE c.IdMasa=? ORDER BY c.DataOra DESC", idMasa);
        while (rs.next()) { Comanda c = map(rs); c.setArticole(findArticole(c.getId())); lista.add(c); }
        return lista;
    }

    public void insert(Comanda c) throws SQLException {
        int id = Util.insert(
            "INSERT INTO Comanda (IdMasa, IdClient, Status, DataOra) VALUES (?,?,?,?)",
            c.getIdMasa(), c.getIdClient(), c.getStatus().getValue(),
            Timestamp.valueOf(c.getDataOra()));
        c.setId(id);
        for (ArticolComanda a : c.getArticole()) { a.setIdComanda(id); insertArticol(a); }
    }

    public void updateStatus(int id, OrderStatus status) throws SQLException {
        Util.update("UPDATE Comanda SET Status=? WHERE IdComanda=?", status.getValue(), id);
    }

    public void delete(int id) throws SQLException {
        Util.update("DELETE FROM ArticolComanda WHERE IdComanda=?", id);
        Util.update("DELETE FROM Comanda WHERE IdComanda=?", id);
    }

    private void insertArticol(ArticolComanda a) throws SQLException {
        int id = Util.insert(
            "INSERT INTO ArticolComanda (IdComanda, IdMancare, Cantitate, PretUnitar) VALUES (?,?,?,?)",
            a.getIdComanda(), a.getIdMancare(), a.getCantitate(), a.getPretUnitar());
        a.setIdArticol(id);
    }

    public List<ArticolComanda> findArticole(int idComanda) throws SQLException {
        List<ArticolComanda> lista = new ArrayList<>();
        ResultSet rs = Util.query(
            "SELECT ac.IdArticol, ac.IdComanda, ac.IdMancare, mn.Denumire, ac.Cantitate, ac.PretUnitar " +
            "FROM ArticolComanda ac JOIN Mancare mn ON mn.IdMancare=ac.IdMancare WHERE ac.IdComanda=?",
            idComanda);
        while (rs.next()) {
            lista.add(new ArticolComanda(rs.getInt("IdArticol"), rs.getInt("IdComanda"),
                    rs.getInt("IdMancare"), rs.getString("Denumire"),
                    rs.getInt("Cantitate"), rs.getBigDecimal("PretUnitar")));
        }
        return lista;
    }

    private Comanda map(ResultSet rs) throws SQLException {
        return new Comanda(rs.getInt("IdComanda"), rs.getInt("IdMasa"), rs.getInt("NumarMasa"),
                rs.getInt("IdClient"), rs.getString("Nume"),
                OrderStatus.from(rs.getString("Status")),
                rs.getTimestamp("DataOra").toLocalDateTime());
    }
}