package com.restaurant.reports;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.restaurant.dao.ComandaDAO;
import com.restaurant.dao.MasaDAO;
import com.restaurant.enums.OrderStatus;
import com.restaurant.model.ArticolComanda;
import com.restaurant.model.Comanda;
import com.restaurant.model.Masa;

public class RaportService {
    private final ComandaDAO comandaDAO = new ComandaDAO();
    private final MasaDAO masaDAO = new MasaDAO();

    // Raport 1 - venituri pe status
    public Map<String, BigDecimal> venituriPeStatus() throws SQLException {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) map.put(s.getValue(), BigDecimal.ZERO);
        for (Comanda c : comandaDAO.findAll())
            map.merge(c.getStatus().getValue(), c.getTotal(), BigDecimal::add);
        return map;
    }

    // Raport 2 - mancari populare: [denumire, cantitate, venit]
    public List<String[]> mancariPopulare() throws SQLException {
        Map<String, int[]> contor = new LinkedHashMap<>();
        for (Comanda c : comandaDAO.findAll()) {
            if (c.getStatus() == OrderStatus.ANULATA) continue;
            for (ArticolComanda a : c.getArticole()) {
                contor.computeIfAbsent(a.getDenumireMancare(), k -> new int[]{0, 0});
                contor.get(a.getDenumireMancare())[0] += a.getCantitate();
                contor.get(a.getDenumireMancare())[1] +=
                        a.getSubtotal().multiply(BigDecimal.valueOf(100)).intValue();
            }
        }
        List<String[]> rezultat = new ArrayList<>();
        contor.entrySet().stream()
              .sorted((a, b) -> b.getValue()[0] - a.getValue()[0])
              .forEach(e -> rezultat.add(new String[]{
                      e.getKey(),
                      String.valueOf(e.getValue()[0]),
                      String.format("%.2f", e.getValue()[1] / 100.0)
              }));
        return rezultat;
    }

    // Raport 3 - statistici mese: [nr, capacitate, total, servite, venituri, status]
    public List<String[]> statisticiMese() throws SQLException {
        List<String[]> rezultat = new ArrayList<>();
        for (Masa masa : masaDAO.findAll()) {
            List<Comanda> comenzi = comandaDAO.findByMasa(masa.getId());
            BigDecimal venituri = BigDecimal.ZERO;
            int servite = 0;
            for (Comanda c : comenzi) {
                if (c.getStatus() != OrderStatus.ANULATA) venituri = venituri.add(c.getTotal());
                if (c.getStatus() == OrderStatus.SERVITA) servite++;
            }
            rezultat.add(new String[]{
                    String.valueOf(masa.getNumarMasa()),
                    String.valueOf(masa.getCapacitate()),
                    String.valueOf(comenzi.size()),
                    String.valueOf(servite),
                    String.format("%.2f", venituri),
                    masa.isOcupata() ? "Ocupata" : "Libera"
            });
        }
        return rezultat;
    }

    // Raport 4 - comenzi active
    public List<Comanda> comenziActive() throws SQLException {
        List<Comanda> active = new ArrayList<>();
        for (Comanda c : comandaDAO.findAll()) {
            OrderStatus s = c.getStatus();
            if (s == OrderStatus.NOUA || s == OrderStatus.IN_PREPARARE || s == OrderStatus.GATA)
                active.add(c);
        }
        return active;
    }
}