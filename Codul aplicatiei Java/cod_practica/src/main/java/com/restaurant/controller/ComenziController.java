package com.restaurant.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.restaurant.dao.ClientDAO;
import com.restaurant.dao.ComandaDAO;
import com.restaurant.dao.MancareDAO;
import com.restaurant.dao.MasaDAO;
import com.restaurant.enums.OrderStatus;
import com.restaurant.model.ArticolComanda;
import com.restaurant.model.Client;
import com.restaurant.model.Comanda;
import com.restaurant.model.Mancare;
import com.restaurant.model.Masa;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ComenziController extends BorderPane {
    private final ComandaDAO comandaDAO = new ComandaDAO();
    private final MasaDAO masaDAO = new MasaDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final MancareDAO mancareDAO = new MancareDAO();

    private final ObservableList<Comanda> date = FXCollections.observableArrayList();
    private final TableView<Comanda> tabel = new TableView<>(date);
    private final ObservableList<ArticolComanda> dateArt = FXCollections.observableArrayList();
    private final TableView<ArticolComanda> tabelArt = new TableView<>(dateArt);
    private final Label lblStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public ComenziController(Label lblStatus) {
        this.lblStatus = lblStatus;
        setTop(buildToolbar());
        SplitPane split = new SplitPane();
        split.getItems().addAll(buildTabelComenzi(), buildTabelArticole());
        split.setDividerPositions(0.6);
        setCenter(split);
        reload();
    }

    private VBox buildToolbar() {
        Button btnNew    = new Button("Comanda noua");
        Button btnStatus = new Button("Schimba status");
        Button btnDel    = new Button("Sterge");
        btnNew.setOnAction(e -> dialogComandaNoua());
        btnStatus.setOnAction(e -> dialogStatus());
        btnDel.setOnAction(e -> delete());

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox barActiuni = new HBox(6, new Label("Comenzi"), sp, btnNew, btnStatus, btnDel);
        barActiuni.setPadding(new Insets(8)); barActiuni.getStyleClass().add("toolbar");

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.setPromptText("Toate statusurile");
        cbStatus.getItems().add(null);
        for (OrderStatus s : OrderStatus.values()) cbStatus.getItems().add(s.getValue());
        cbStatus.setOnAction(e -> {
            try {
                String v = cbStatus.getValue();
                date.setAll(v == null ? comandaDAO.findAll() : comandaDAO.findByStatus(OrderStatus.from(v)));
                dateArt.clear();
            } catch (SQLException ex) { err(ex.getMessage()); }
        });

        HBox barFiltru = new HBox(6, new Label("Status:"), cbStatus);
        barFiltru.setPadding(new Insets(0, 8, 6, 8));
        return new VBox(barActiuni, barFiltru);
    }

    @SuppressWarnings("unchecked")
    private TableView<Comanda> buildTabelComenzi() {
        TableColumn<Comanda, Integer> colId     = new TableColumn<>("ID");
        TableColumn<Comanda, Integer> colMasa   = new TableColumn<>("Masa");
        TableColumn<Comanda, String>  colClient = new TableColumn<>("Client");
        TableColumn<Comanda, String>  colStatus = new TableColumn<>("Status");
        TableColumn<Comanda, String>  colData   = new TableColumn<>("Data");
        TableColumn<Comanda, String>  colTotal  = new TableColumn<>("Total");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMasa.setCellValueFactory(new PropertyValueFactory<>("numarMasa"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("numeClient"));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().getValue()));
        colData.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDataOra() != null ? d.getValue().getDataOra().format(FMT) : "-"));
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%.2f", d.getValue().getTotal())));
        tabel.getColumns().addAll(colId, colMasa, colClient, colStatus, colData, colTotal);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabel.getSelectionModel().selectedItemProperty().addListener(
                (o, v, sel) -> dateArt.setAll(
                        sel != null ? sel.getArticole() : FXCollections.emptyObservableList()));
        return tabel;
    }

    @SuppressWarnings("unchecked")
    private VBox buildTabelArticole() {
        TableColumn<ArticolComanda, String>     colDen  = new TableColumn<>("Mancare");
        TableColumn<ArticolComanda, Integer>    colCant = new TableColumn<>("Cant.");
        TableColumn<ArticolComanda, BigDecimal> colPret = new TableColumn<>("Pret unit.");
        TableColumn<ArticolComanda, String>     colSub  = new TableColumn<>("Subtotal");
        colDen.setCellValueFactory(new PropertyValueFactory<>("denumireMancare"));
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        colPret.setCellValueFactory(new PropertyValueFactory<>("pretUnitar"));
        colSub.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%.2f", d.getValue().getSubtotal())));
        tabelArt.getColumns().addAll(colDen, colCant, colPret, colSub);
        tabelArt.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox box = new VBox(4, new Label("Articole comanda"), tabelArt);
        box.setPadding(new Insets(4)); VBox.setVgrow(tabelArt, Priority.ALWAYS);
        return box;
    }

    private void reload() {
        try { date.setAll(comandaDAO.findAll()); dateArt.clear(); lblStatus.setText("Comenzi: " + date.size()); }
        catch (SQLException e) { err(e.getMessage()); }
    }

    private void dialogComandaNoua() {
        ComboBox<Masa>    cbMasa   = new ComboBox<>();
        ComboBox<Client>  cbClient = new ComboBox<>();
        ListView<Mancare> lvMan    = new ListView<>();
        lvMan.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvMan.setPrefHeight(150);
        try {
            cbMasa.getItems().addAll(masaDAO.findAll());
            cbClient.getItems().addAll(clientDAO.findAll());
            lvMan.getItems().addAll(mancareDAO.findDisponibile());
        } catch (SQLException e) { err(e.getMessage()); return; }
        cbMasa.setPromptText("Selectati masa");
        cbClient.setPromptText("Selectati clientul");

        Dialog<Boolean> dlg = new Dialog<>();
        dlg.setTitle("Comanda noua");
        dlg.getDialogPane().setContent(new VBox(6,
                new Label("Masa:"), cbMasa, new Label("Client:"), cbClient,
                new Label("Mancari (Ctrl+Click):"), lvMan));
        dlg.getDialogPane().setPrefWidth(400);
        ButtonType ok = new ButtonType("Salveaza", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b == ok);
        dlg.showAndWait().ifPresent(confirmed -> {
            if (!confirmed) return;
            if (cbMasa.getValue() == null || cbClient.getValue() == null
                    || lvMan.getSelectionModel().getSelectedItems().isEmpty()) {
                err("Completati toate campurile."); return;
            }
            Comanda c = new Comanda();
            c.setIdMasa(cbMasa.getValue().getId());
            c.setNumarMasa(cbMasa.getValue().getNumarMasa());
            c.setIdClient(cbClient.getValue().getId());
            c.setNumeClient(cbClient.getValue().getNume());
            c.setDataOra(LocalDateTime.now());
            for (Mancare m : lvMan.getSelectionModel().getSelectedItems()) {
                ArticolComanda a = new ArticolComanda();
                a.setIdMancare(m.getId()); a.setDenumireMancare(m.getDenumire());
                a.setCantitate(1); a.setPretUnitar(m.getPret());
                c.getArticole().add(a);
            }
            try {
                comandaDAO.insert(c);
                masaDAO.setOcupata(c.getIdMasa(), true);
                reload();
                lblStatus.setText("Comanda #" + c.getId() + " creata.");
            } catch (SQLException e) { err(e.getMessage()); }
        });
    }

    private void dialogStatus() {
        Comanda sel = tabel.getSelectionModel().getSelectedItem();
        if (sel == null) { err("Selectati o comanda."); return; }
        ChoiceDialog<OrderStatus> dlg = new ChoiceDialog<>(sel.getStatus(), OrderStatus.values());
        dlg.setTitle("Schimba status");
        dlg.setHeaderText("Comanda #" + sel.getId());
        dlg.setContentText("Status nou:");
        dlg.showAndWait().ifPresent(statusNou -> {
            try {
                comandaDAO.updateStatus(sel.getId(), statusNou);
                if (statusNou == OrderStatus.SERVITA || statusNou == OrderStatus.ANULATA)
                    masaDAO.setOcupata(sel.getIdMasa(), false);
                reload();
            } catch (SQLException e) { err(e.getMessage()); }
        });
    }

    private void delete() {
        Comanda sel = tabel.getSelectionModel().getSelectedItem();
        if (sel == null) { err("Selectati o comanda."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Stergeti comanda #" + sel.getId() + "?")
            .showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                try {
                    comandaDAO.delete(sel.getId());
                    masaDAO.setOcupata(sel.getIdMasa(), false);
                    reload();
                } catch (SQLException e) { err(e.getMessage()); }
            });
    }

    private void err(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}