package com.restaurant.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.restaurant.dao.ClientDAO;
import com.restaurant.dao.ComandaDAO;
import com.restaurant.dao.MancareDAO;
import com.restaurant.dao.MasaDAO;
import com.restaurant.model.Comanda;
import com.restaurant.reports.RaportService;
import com.restaurant.service.ExportService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RapoarteController extends BorderPane {
    private final RaportService raportService = new RaportService();
    private final ComandaDAO comandaDAO = new ComandaDAO();
    private final MancareDAO mancareDAO = new MancareDAO();
    private final MasaDAO masaDAO = new MasaDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final Label lblStatus;

    public RapoarteController(Label lblStatus) {
        this.lblStatus = lblStatus;
        HBox header = new HBox(new Label("Rapoarte si Export"));
        header.setPadding(new Insets(8)); header.getStyleClass().add("toolbar");
        setTop(header);
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                new Tab("Mancari populare",   buildRaport2()),
                new Tab("Statistici mese",    buildRaport3()),
                new Tab("Comenzi active",     buildRaport4()),
                new Tab("Venituri pe status", buildRaport1()),
                new Tab("Export date",        buildExport())
        );
        setCenter(tabs);
    }

    // ── Raport 1 — venituri pe status ────────────────────────────────────

    private Pane buildRaport1() {
        TableView<String[]> tabel = new TableView<>();
        TableColumn<String[], String> colStatus = new TableColumn<>("Status");
        TableColumn<String[], String> colVen    = new TableColumn<>("Venituri MDL");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colVen.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[1]));
        tabel.getColumns().addAll(colStatus, colVen);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnGen = new Button("Genereaza");
        Button btnCsv = new Button("Export CSV");
        Button btnTxt = new Button("Export TXT");
        btnCsv.setDisable(true); btnTxt.setDisable(true);

        // tinem datele ca sa le refolosim la export
        Map<String, BigDecimal>[] ref = new Map[1];

        btnGen.setOnAction(e -> {
            try {
                ref[0] = raportService.venituriPeStatus();
                List<String[]> rows = new ArrayList<>();
                for (Map.Entry<String, BigDecimal> en : ref[0].entrySet())
                    rows.add(new String[]{en.getKey(), String.format("%.2f", en.getValue())});
                tabel.setItems(FXCollections.observableArrayList(rows));
                btnCsv.setDisable(false); btnTxt.setDisable(false);
                lblStatus.setText("Raport generat.");
            } catch (SQLException ex) { err(ex.getMessage()); }
        });

        btnCsv.setOnAction(e -> {
            try { info("Salvat: " + ExportService.exportVenituriPeStatusCsv(ref[0])); }
            catch (IOException ex) { err(ex.getMessage()); }
        });
        btnTxt.setOnAction(e -> {
            try { info("Salvat: " + ExportService.exportVenituriPeStatusTxt(ref[0])); }
            catch (IOException ex) { err(ex.getMessage()); }
        });

        HBox btns = new HBox(6, btnGen, btnCsv, btnTxt);
        VBox pane = new VBox(6, btns, tabel);
        pane.setPadding(new Insets(10)); VBox.setVgrow(tabel, Priority.ALWAYS);
        return pane;
    }

    // ── Raport 2 — mancari populare ───────────────────────────────────────

    private Pane buildRaport2() {
        TableView<String[]> tabel = new TableView<>();
        TableColumn<String[], String> colDen  = new TableColumn<>("Mancare");
        TableColumn<String[], String> colCant = new TableColumn<>("Cantitate");
        TableColumn<String[], String> colVen  = new TableColumn<>("Venituri MDL");
        colDen.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[0]));
        colCant.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colVen.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[2]));
        tabel.getColumns().addAll(colDen, colCant, colVen);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnGen = new Button("Genereaza");
        Button btnCsv = new Button("Export CSV");
        Button btnTxt = new Button("Export TXT");
        btnCsv.setDisable(true); btnTxt.setDisable(true);

        List<String[]>[] ref = new List[1];

        btnGen.setOnAction(e -> {
            try {
                ref[0] = raportService.mancariPopulare();
                tabel.setItems(FXCollections.observableArrayList(ref[0]));
                btnCsv.setDisable(false); btnTxt.setDisable(false);
                lblStatus.setText("Raport generat.");
            } catch (SQLException ex) { err(ex.getMessage()); }
        });
        btnCsv.setOnAction(e -> {
            try { info("Salvat: " + ExportService.exportMancariPopulareCsv(ref[0])); }
            catch (IOException ex) { err(ex.getMessage()); }
        });
        btnTxt.setOnAction(e -> {
            try { info("Salvat: " + ExportService.exportMancariPopulareTxt(ref[0])); }
            catch (IOException ex) { err(ex.getMessage()); }
        });

        HBox btns = new HBox(6, btnGen, btnCsv, btnTxt);
        VBox pane = new VBox(6, btns, tabel);
        pane.setPadding(new Insets(10)); VBox.setVgrow(tabel, Priority.ALWAYS);
        return pane;
    }

    // ── Raport 3 — statistici mese ────────────────────────────────────────

    private Pane buildRaport3() {
        TableView<String[]> tabel = new TableView<>();
        String[] cols = {"Nr. Masa", "Capacitate", "Total comenzi", "Servite", "Venituri MDL", "Status"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabel.getColumns().add(col);
        }
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnGen = new Button("Genereaza");
        Button btnCsv = new Button("Export CSV");
        Button btnTxt = new Button("Export TXT");
        btnCsv.setDisable(true); btnTxt.setDisable(true);

        List<String[]>[] ref = new List[1];

        btnGen.setOnAction(e -> {
            try {
                ref[0] = raportService.statisticiMese();
                tabel.setItems(FXCollections.observableArrayList(ref[0]));
                btnCsv.setDisable(false); btnTxt.setDisable(false);
                lblStatus.setText("Raport generat.");
            } catch (SQLException ex) { err(ex.getMessage()); }
        });
        btnCsv.setOnAction(e -> {
            try { info("Salvat: " + ExportService.exportStatisticiMeseCsv(ref[0])); }
            catch (IOException ex) { err(ex.getMessage()); }
        });
        btnTxt.setOnAction(e -> {
            try { info("Salvat: " + ExportService.exportStatisticiMeseTxt(ref[0])); }
            catch (IOException ex) { err(ex.getMessage()); }
        });

        HBox btns = new HBox(6, btnGen, btnCsv, btnTxt);
        VBox pane = new VBox(6, btns, tabel);
        pane.setPadding(new Insets(10)); VBox.setVgrow(tabel, Priority.ALWAYS);
        return pane;
    }

    // ── Raport 4 — comenzi active ─────────────────────────────────────────

    private Pane buildRaport4() {
        TableView<Comanda> tabel = new TableView<>();
        TableColumn<Comanda, Integer> colId   = new TableColumn<>("ID");
        TableColumn<Comanda, Integer> colMasa = new TableColumn<>("Masa");
        TableColumn<Comanda, String>  colCl   = new TableColumn<>("Client");
        TableColumn<Comanda, String>  colSt   = new TableColumn<>("Status");
        TableColumn<Comanda, String>  colTot  = new TableColumn<>("Total MDL");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMasa.setCellValueFactory(new PropertyValueFactory<>("numarMasa"));
        colCl.setCellValueFactory(new PropertyValueFactory<>("numeClient"));
        colSt.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().getValue()));
        colTot.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%.2f", d.getValue().getTotal())));
        tabel.getColumns().addAll(colId, colMasa, colCl, colSt, colTot);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnGen = new Button("Genereaza");
        Button btnCsv = new Button("Export CSV");
        Button btnTxt = new Button("Export TXT");
        btnCsv.setDisable(true); btnTxt.setDisable(true);

        List<Comanda>[] ref = new List[1];

        btnGen.setOnAction(e -> {
            try {
                ref[0] = raportService.comenziActive();
                tabel.setItems(FXCollections.observableArrayList(ref[0]));
                btnCsv.setDisable(false); btnTxt.setDisable(false);
                lblStatus.setText("Comenzi active: " + ref[0].size());
            } catch (SQLException ex) { err(ex.getMessage()); }
        });
        btnCsv.setOnAction(e -> {
            try { info("Salvat: " + ExportService.exportComenziActiveCsv(ref[0])); }
            catch (IOException ex) { err(ex.getMessage()); }
        });
        btnTxt.setOnAction(e -> {
            try { info("Salvat: " + ExportService.exportComenziActiveTxt(ref[0])); }
            catch (IOException ex) { err(ex.getMessage()); }
        });

        HBox btns = new HBox(6, btnGen, btnCsv, btnTxt);
        VBox pane = new VBox(6, btns, tabel);
        pane.setPadding(new Insets(10)); VBox.setVgrow(tabel, Priority.ALWAYS);
        return pane;
    }

    // ── Export date brute ─────────────────────────────────────────────────

    private Pane buildExport() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(16));

        pane.getChildren().addAll(
                buildExportRow("Mancari",  this::exportMancariCsv,  this::exportMancariTxt),
                buildExportRow("Mese",     this::exportMeseCsv,     this::exportMeseTxt),
                buildExportRow("Clienti",  this::exportClientiCsv,  this::exportClientiTxt),
                buildExportRow("Comenzi",  this::exportComenziCsv,  this::exportComenziTxt)
        );

        return pane;
    }

    private HBox buildExportRow(String entitate, Runnable csv, Runnable txt) {
        Label lbl    = new Label(entitate + ":");
        lbl.setMinWidth(70);
        Button btnCsv = new Button("CSV");
        Button btnTxt = new Button("TXT");
        btnCsv.setOnAction(e -> csv.run());
        btnTxt.setOnAction(e -> txt.run());
        return new HBox(8, lbl, btnCsv, btnTxt);
    }

    private void exportMancariCsv() {
        try { info("Salvat: " + ExportService.exportMancariCsv(mancareDAO.findAll())); }
        catch (IOException | SQLException e) { err(e.getMessage()); }
    }
    private void exportMancariTxt() {
        try { info("Salvat: " + ExportService.exportMancariTxt(mancareDAO.findAll())); }
        catch (IOException | SQLException e) { err(e.getMessage()); }
    }
    private void exportMeseCsv() {
        try { info("Salvat: " + ExportService.exportMeseCsv(masaDAO.findAll())); }
        catch (IOException | SQLException e) { err(e.getMessage()); }
    }
    private void exportMeseTxt() {
        try { info("Salvat: " + ExportService.exportMeseTxt(masaDAO.findAll())); }
        catch (IOException | SQLException e) { err(e.getMessage()); }
    }
    private void exportClientiCsv() {
        try { info("Salvat: " + ExportService.exportClientiCsv(clientDAO.findAll())); }
        catch (IOException | SQLException e) { err(e.getMessage()); }
    }
    private void exportClientiTxt() {
        try { info("Salvat: " + ExportService.exportClientiTxt(clientDAO.findAll())); }
        catch (IOException | SQLException e) { err(e.getMessage()); }
    }
    private void exportComenziCsv() {
        try { info("Salvat: " + ExportService.exportComenziCsv(comandaDAO.findAll())); }
        catch (IOException | SQLException e) { err(e.getMessage()); }
    }
    private void exportComenziTxt() {
        try { info("Salvat: " + ExportService.exportComenziTxt(comandaDAO.findAll())); }
        catch (IOException | SQLException e) { err(e.getMessage()); }
    }

    private void err(String msg)  { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void info(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
}