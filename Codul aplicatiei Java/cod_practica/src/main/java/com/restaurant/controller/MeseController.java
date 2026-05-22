package com.restaurant.controller;

import java.sql.SQLException;

import com.restaurant.dao.MasaDAO;
import com.restaurant.model.Masa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MeseController extends BorderPane {
    private final MasaDAO dao = new MasaDAO();
    private final ObservableList<Masa> date = FXCollections.observableArrayList();
    private final TableView<Masa> tabel = new TableView<>(date);
    private final Label lblStatus;

    public MeseController(Label lblStatus) {
        this.lblStatus = lblStatus;
        setTop(buildToolbar());
        setCenter(tabel);
        buildTabel();
        reload();
    }

    private HBox buildToolbar() {
        Button btnAdd  = new Button("Adauga");
        Button btnEdit = new Button("Editeaza");
        Button btnDel  = new Button("Sterge");
        btnAdd.setOnAction(e -> dialogAdd());
        btnEdit.setOnAction(e -> dialogEdit());
        btnDel.setOnAction(e -> delete());
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox bar = new HBox(6, new Label("Mese"), sp, btnAdd, btnEdit, btnDel);
        bar.setPadding(new Insets(8)); bar.getStyleClass().add("toolbar");
        return bar;
    }

    @SuppressWarnings("unchecked")
    private void buildTabel() {
        TableColumn<Masa, Integer> colNr  = new TableColumn<>("Nr. Masa");
        TableColumn<Masa, Integer> colCap = new TableColumn<>("Capacitate");
        TableColumn<Masa, Boolean> colOc  = new TableColumn<>("Status");
        colNr.setCellValueFactory(new PropertyValueFactory<>("numarMasa"));
        colCap.setCellValueFactory(new PropertyValueFactory<>("capacitate"));
        colOc.setCellValueFactory(new PropertyValueFactory<>("ocupata"));
        colOc.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : (v ? "Ocupata" : "Libera"));
            }
        });
        tabel.getColumns().addAll(colNr, colCap, colOc);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void reload() {
        try { date.setAll(dao.findAll()); lblStatus.setText("Mese: " + date.size()); }
        catch (SQLException e) { err(e.getMessage()); }
    }

    private void dialogAdd() {
        TextField tfNr  = new TextField(); tfNr.setPromptText("Nr. masa");
        TextField tfCap = new TextField(); tfCap.setPromptText("Capacitate");
        if (!dialog("Adauga masa", new VBox(6, new Label("Nr:"), tfNr, new Label("Capacitate:"), tfCap))) return;
        try {
            dao.insert(new Masa(0, Integer.parseInt(tfNr.getText().trim()),
                    Integer.parseInt(tfCap.getText().trim()), false));
            reload();
        } catch (NumberFormatException | SQLException e) { err(e.getMessage()); }
    }

    private void dialogEdit() {
        Masa sel = tabel.getSelectionModel().getSelectedItem();
        if (sel == null) { err("Selectati o masa."); return; }
        TextField tfNr  = new TextField(String.valueOf(sel.getNumarMasa()));
        TextField tfCap = new TextField(String.valueOf(sel.getCapacitate()));
        CheckBox cbOc = new CheckBox("Ocupata"); cbOc.setSelected(sel.isOcupata());
        if (!dialog("Editeaza masa", new VBox(6, new Label("Nr:"), tfNr, new Label("Capacitate:"), tfCap, cbOc))) return;
        try {
            sel.setNumarMasa(Integer.parseInt(tfNr.getText().trim()));
            sel.setCapacitate(Integer.parseInt(tfCap.getText().trim()));
            sel.setOcupata(cbOc.isSelected());
            dao.update(sel); reload();
        } catch (NumberFormatException | SQLException e) { err(e.getMessage()); }
    }

    private void delete() {
        Masa sel = tabel.getSelectionModel().getSelectedItem();
        if (sel == null) { err("Selectati o masa."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Stergeti masa #" + sel.getNumarMasa() + "?")
            .showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                try { dao.delete(sel.getId()); reload(); } catch (SQLException e) { err(e.getMessage()); }
            });
    }

    private boolean dialog(String titlu, VBox content) {
        Dialog<Boolean> dlg = new Dialog<>();
        dlg.setTitle(titlu);
        dlg.getDialogPane().setContent(content);
        ButtonType ok = new ButtonType("Salveaza", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b == ok);
        return dlg.showAndWait().orElse(false);
    }

    private void err(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}