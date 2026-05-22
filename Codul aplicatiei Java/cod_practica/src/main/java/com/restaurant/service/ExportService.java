package com.restaurant.service;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.restaurant.model.ArticolComanda;
import com.restaurant.model.Client;
import com.restaurant.model.Comanda;
import com.restaurant.model.Mancare;
import com.restaurant.model.Masa;

public class ExportService {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // ── Mancaruri ───────────────────────────────────────────────────────────

    public static String exportMancariCsv(List<Mancare> mancari) throws IOException {
        String file = "mancari_" + now() + ".csv";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("ID,Denumire,Categorie,Pret MDL,Disponibila\n");
            for (Mancare m : mancari)
                fw.write(m.getId() + "," + m.getDenumire() + "," + m.getCategorie() + ","
                        + m.getPret() + "," + (m.isDisponibila() ? "Da" : "Nu") + "\n");
        }
        return file;
    }

    public static String exportMancariTxt(List<Mancare> mancari) throws IOException {
        String file = "mancari_" + now() + ".txt";
        try (FileWriter fw = new FileWriter(file)) {
            header(fw, "Meniu complet");
            String catCurenta = "";
            for (Mancare m : mancari) {
                if (!m.getCategorie().equals(catCurenta)) {
                    catCurenta = m.getCategorie();
                    fw.write("\n[ " + catCurenta + " ]\n");
                }
                fw.write(String.format("  %-30s %8.2f MDL   %s%n",
                        m.getDenumire(), m.getPret(),
                        m.isDisponibila() ? "disponibil" : "indisponibil"));
            }
            fw.write("\nTotal produse: " + mancari.size() + "\n");
        }
        return file;
    }

    // ── Mese ─────────────────────────────────────────────────────────────

    public static String exportMeseCsv(List<Masa> mese) throws IOException {
        String file = "mese_" + now() + ".csv";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("ID,Nr. Masa,Capacitate,Status\n");
            for (Masa m : mese)
                fw.write(m.getId() + "," + m.getNumarMasa() + "," + m.getCapacitate() + ","
                        + (m.isOcupata() ? "Ocupata" : "Libera") + "\n");
        }
        return file;
    }

    public static String exportMeseTxt(List<Masa> mese) throws IOException {
        String file = "mese_" + now() + ".txt";
        try (FileWriter fw = new FileWriter(file)) {
            header(fw, "Situatie mese");
            int libere = 0, ocupate = 0;
            for (Masa m : mese) {
                fw.write(String.format("  Masa #%-3d | Capacitate: %2d | %s%n",
                        m.getNumarMasa(), m.getCapacitate(),
                        m.isOcupata() ? "Ocupata" : "Libera"));
                if (m.isOcupata()) ocupate++; else libere++;
            }   fw.write("\nTotal: " + mese.size() + "  |  Libere: " + libere + "  |  Ocupate: " + ocupate + "\n");
        }
        return file;
    }

    // ── Clienti ───────────────────────────────────────────────────────────

    public static String exportClientiCsv(List<Client> clienti) throws IOException {
        String file = "clienti_" + now() + ".csv";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("ID,Nume,Telefon,Email\n");
            for (Client c : clienti)
                fw.write(c.getId() + "," + c.getNume() + "," + c.getTelefon() + "," + c.getEmail() + "\n");
        }
        return file;
    }

    public static String exportClientiTxt(List<Client> clienti) throws IOException {
        String file = "clienti_" + now() + ".txt";
        try (FileWriter fw = new FileWriter(file)) {
            header(fw, "Lista clienti");
            for (Client c : clienti)
                fw.write(String.format("  [%3d] %-30s %-15s %s%n",
                        c.getId(), c.getNume(), c.getTelefon(), c.getEmail()));
            fw.write("\nTotal clienti: " + clienti.size() + "\n");
        }
        return file;
    }

    // ── Comenzi ───────────────────────────────────────────────────────────

    public static String exportComenziCsv(List<Comanda> comenzi) throws IOException {
        String file = "comenzi_" + now() + ".csv";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("ID,Masa,Client,Status,Data,Total MDL\n");
            for (Comanda c : comenzi)
                fw.write(c.getId() + "," + c.getNumarMasa() + "," + c.getNumeClient() + ","
                        + c.getStatus().getValue() + ","
                        + (c.getDataOra() != null ? c.getDataOra().format(DT) : "-") + ","
                        + c.getTotal() + "\n");
        }
        return file;
    }

    public static String exportComenziTxt(List<Comanda> comenzi) throws IOException {
        String file = "comenzi_" + now() + ".txt";
        try (FileWriter fw = new FileWriter(file)) {
            header(fw, "Lista comenzi");
            BigDecimal totalGeneral = BigDecimal.ZERO;
            for (Comanda c : comenzi) {
                fw.write(String.format("  Comanda #%d | Masa #%d | %-25s | %-12s | %s%n",
                        c.getId(), c.getNumarMasa(), c.getNumeClient(),
                        c.getStatus().getValue(),
                        c.getDataOra() != null ? c.getDataOra().format(DT) : "-"));
                for (ArticolComanda a : c.getArticole())
                    fw.write(String.format("    - %-28s x%d  %8.2f MDL%n",
                            a.getDenumireMancare(), a.getCantitate(), a.getSubtotal()));
                fw.write(String.format("    Total: %.2f MDL%n%n", c.getTotal()));
                totalGeneral = totalGeneral.add(c.getTotal());
            }
            fw.write("-".repeat(60) + "\n");
            fw.write(String.format("Total general: %.2f MDL | Comenzi: %d%n", totalGeneral, comenzi.size()));
        }
        return file;
    }

    // ── Rapoarte ──────────────────────────────────────────────────────────

    public static String exportMancariPopulareCsv(List<String[]> date) throws IOException {
        String file = "raport_mancari_populare_" + now() + ".csv";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Mancare,Cantitate totala,Venituri MDL\n");
            for (String[] r : date) fw.write(r[0] + "," + r[1] + "," + r[2] + "\n");
        }
        return file;
    }

    public static String exportMancariPopulareTxt(List<String[]> date) throws IOException {
        String file = "raport_mancari_populare_" + now() + ".txt";
        try (FileWriter fw = new FileWriter(file)) {
            header(fw, "Mancari populare");
            fw.write(String.format("  %-30s %10s %15s%n", "Mancare", "Cantitate", "Venituri MDL"));
            fw.write("-".repeat(60) + "\n");
            for (String[] r : date)
                fw.write(String.format("  %-30s %10s %15s%n", r[0], r[1], r[2]));
        }
        return file;
    }

    public static String exportStatisticiMeseCsv(List<String[]> date) throws IOException {
        String file = "raport_mese_" + now() + ".csv";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Nr. Masa,Capacitate,Total comenzi,Servite,Venituri MDL,Status\n");
            for (String[] r : date) fw.write(r[0]+","+r[1]+","+r[2]+","+r[3]+","+r[4]+","+r[5]+"\n");
        }
        return file;
    }

    public static String exportStatisticiMeseTxt(List<String[]> date) throws IOException {
        String file = "raport_mese_" + now() + ".txt";
        try (FileWriter fw = new FileWriter(file)) {
            header(fw, "Statistici mese");
            fw.write(String.format("  %-8s %-10s %-14s %-8s %-15s %s%n",
                    "Masa", "Cap.", "Total comenzi", "Servite", "Venituri MDL", "Status"));
            fw.write("-".repeat(70) + "\n");
            for (String[] r : date)
                fw.write(String.format("  %-8s %-10s %-14s %-8s %-15s %s%n",
                        r[0], r[1], r[2], r[3], r[4], r[5]));
        }
        return file;
    }

    public static String exportVenituriPeStatusCsv(Map<String, BigDecimal> date) throws IOException {
        String file = "raport_venituri_" + now() + ".csv";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Status,Venituri MDL\n");
            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> e : date.entrySet()) {
                fw.write(e.getKey() + "," + e.getValue() + "\n");
                total = total.add(e.getValue());
            }
            fw.write("TOTAL," + total + "\n");
        }
        return file;
    }

    public static String exportVenituriPeStatusTxt(Map<String, BigDecimal> date) throws IOException {
        String file = "raport_venituri_" + now() + ".txt";
        try (FileWriter fw = new FileWriter(file)) {
            header(fw, "Venituri pe status comanda");
            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> e : date.entrySet()) {
                fw.write(String.format("  %-20s : %10.2f MDL%n", e.getKey(), e.getValue()));
                total = total.add(e.getValue());
            }
            fw.write("-".repeat(40) + "\n");
            fw.write(String.format("  %-20s : %10.2f MDL%n", "TOTAL", total));
        }
        return file;
    }

    public static String exportComenziActiveCsv(List<Comanda> comenzi) throws IOException {
        String file = "raport_comenzi_active_" + now() + ".csv";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("ID,Masa,Client,Status,Total MDL\n");
            for (Comanda c : comenzi)
                fw.write(c.getId() + "," + c.getNumarMasa() + "," + c.getNumeClient() + ","
                        + c.getStatus().getValue() + "," + c.getTotal() + "\n");
        }
        return file;
    }

    public static String exportComenziActiveTxt(List<Comanda> comenzi) throws IOException {
        String file = "raport_comenzi_active_" + now() + ".txt";
        try (FileWriter fw = new FileWriter(file)) {
            header(fw, "Comenzi active");
            BigDecimal total = BigDecimal.ZERO;
            for (Comanda c : comenzi) {
                fw.write(String.format("  #%-4d Masa #%-3d %-25s %-14s %.2f MDL%n",
                        c.getId(), c.getNumarMasa(), c.getNumeClient(),
                        c.getStatus().getValue(), c.getTotal()));
                total = total.add(c.getTotal());
            }
            fw.write("-".repeat(60) + "\n");
            fw.write(String.format("  Comenzi active: %d | Total: %.2f MDL%n", comenzi.size(), total));
        }
        return file;
    }

    // ── Utilitare private ─────────────────────────────────────────────────

    private static String now() {
        return LocalDateTime.now().format(TS);
    }

    private static void header(FileWriter fw, String titlu) throws IOException {
        fw.write("RESTAURANT - " + titlu + "\n");
        fw.write("Generat: " + LocalDateTime.now().format(DT) + "\n");
        fw.write("=".repeat(60) + "\n\n");
    }
}