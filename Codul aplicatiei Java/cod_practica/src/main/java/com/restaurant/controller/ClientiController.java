package com.restaurant.controller;

import java.sql.SQLException;

import com.restaurant.dao.ClientDAO;
import com.restaurant.model.Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ClientiController extends BorderPane {
    private final ClientDAO dao = new ClientDAO();
    private final ObservableList<Client> date = FXCollections.observableArrayList();
    private final TableView<Client> tabel = new TableView<>(date);
    private final Label lblStatus;

    public ClientiController(Label lblStatus) {
        this.lblStatus = lblStatus;
        setTop(buildTop());
        setCenter(tabel);
        buildTabel();
        reload();
    }

    private VBox buildTop() {
        Button btnAdd  = new Button("Adauga");
        Button btnEdit = new Button("Editeaza");
        Button btnDel  = new Button("Sterge");
        btnAdd.setOnAction(e -> dialogForm(null));
        btnEdit.setOnAction(e -> {
            Client s = tabel.getSelectionModel().getSelectedItem();
            if (s == null) { err("Selectati un client."); return; }
            dialogForm(s);
        });
        btnDel.setOnAction(e -> delete());

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox barActiuni = new HBox(6, new Label("Clienti"), sp, btnAdd, btnEdit, btnDel);
        barActiuni.setPadding(new Insets(8)); barActiuni.getStyleClass().add("toolbar");

        TextField tfCauta = new TextField();
        tfCauta.setPromptText("Cauta dupa nume..."); tfCauta.setPrefWidth(240);
        tfCauta.textProperty().addListener((o, v, n) -> {
            try {
                date.setAll(n.isBlank() ? dao.findAll() : dao.searchByNume(n));
                lblStatus.setText("Rezultate: " + date.size());
            } catch (SQLException e) { err(e.getMessage()); }
        });

        HBox barFiltru = new HBox(6, new Label("Cauta:"), tfCauta);
        barFiltru.setPadding(new Insets(0, 8, 6, 8));
        return new VBox(barActiuni, barFiltru);
    }

    @SuppressWarnings("unchecked")
    private void buildTabel() {
        TableColumn<Client, String> colNume  = new TableColumn<>("Nume");
        TableColumn<Client, String> colTel   = new TableColumn<>("Telefon");
        TableColumn<Client, String> colEmail = new TableColumn<>("Email");
        colNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tabel.getColumns().addAll(colNume, colTel, colEmail);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void reload() {
        try { date.setAll(dao.findAll()); lblStatus.setText("Clienti: " + date.size()); }
        catch (SQLException e) { err(e.getMessage()); }
    }

    private void dialogForm(Client ex) {
        TextField tfNume  = new TextField(ex != null ? ex.getNume() : "");
        TextField tfTel   = new TextField(ex != null ? ex.getTelefon() : "");
        TextField tfEmail = new TextField(ex != null ? ex.getEmail() : "");
        tfNume.setPromptText("Nume"); tfTel.setPromptText("Telefon"); tfEmail.setPromptText("Email");

        Dialog<Boolean> dlg = new Dialog<>();
        dlg.setTitle(ex == null ? "Adauga client" : "Editeaza client");
        dlg.getDialogPane().setContent(new VBox(6,
                new Label("Nume:"), tfNume, new Label("Telefon:"), tfTel,
                new Label("Email:"), tfEmail));
        ButtonType ok = new ButtonType("Salveaza", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b == ok);
        dlg.showAndWait().ifPresent(confirmed -> {
            if (!confirmed) return;
            try {
                if (ex != null) {
                    ex.setNume(tfNume.getText().trim()); ex.setTelefon(tfTel.getText().trim());
                    ex.setEmail(tfEmail.getText().trim()); dao.update(ex);
                } else {
                    dao.insert(new Client(0, tfNume.getText().trim(),
                            tfTel.getText().trim(), tfEmail.getText().trim()));
                }
                reload();
            } catch (SQLException e) { err(e.getMessage()); }
        });
    }

    private void delete() {
        Client sel = tabel.getSelectionModel().getSelectedItem();
        if (sel == null) { err("Selectati un client."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Stergeti '" + sel.getNume() + "'?")
            .showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                try { dao.delete(sel.getId()); reload(); } catch (SQLException e) { err(e.getMessage()); }
            });
    }

    private void err(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}