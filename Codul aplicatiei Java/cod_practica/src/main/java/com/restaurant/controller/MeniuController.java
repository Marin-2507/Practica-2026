package com.restaurant.controller;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.restaurant.dao.MancareDAO;
import com.restaurant.model.Mancare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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

public class MeniuController extends BorderPane {
    private final MancareDAO dao = new MancareDAO();
    private final ObservableList<Mancare> date = FXCollections.observableArrayList();
    private final TableView<Mancare> tabel = new TableView<>(date);
    private final TextField tfCauta = new TextField();
    private final ComboBox<String> cbCat = new ComboBox<>();
    private final Label lblStatus;

    public MeniuController(Label lblStatus) {
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
            Mancare s = tabel.getSelectionModel().getSelectedItem();
            if (s == null) { err("Selectati o mancare."); return; }
            dialogForm(s);
        });
        btnDel.setOnAction(e -> delete());

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox barActiuni = new HBox(6, new Label("Meniu"), sp, btnAdd, btnEdit, btnDel);
        barActiuni.setPadding(new Insets(8)); barActiuni.getStyleClass().add("toolbar");

        tfCauta.setPromptText("Cauta dupa denumire..."); tfCauta.setPrefWidth(200);
        tfCauta.textProperty().addListener((o, v, n) -> filtreaza());
        cbCat.setPromptText("Toate categoriile");
        cbCat.setOnAction(e -> filtreaza());
        try { cbCat.getItems().add(null); cbCat.getItems().addAll(dao.findCategorii()); }
        catch (SQLException ignored) {}

        HBox barFiltru = new HBox(6, new Label("Filtru:"), tfCauta, cbCat);
        barFiltru.setPadding(new Insets(0, 8, 6, 8));
        return new VBox(barActiuni, barFiltru);
    }

    @SuppressWarnings("unchecked")
    private void buildTabel() {
        TableColumn<Mancare, String>     colDen  = new TableColumn<>("Denumire");
        TableColumn<Mancare, String>     colCat  = new TableColumn<>("Categorie");
        TableColumn<Mancare, BigDecimal> colPret = new TableColumn<>("Pret (MDL)");
        TableColumn<Mancare, Boolean>    colDisp = new TableColumn<>("Disponibila");
        colDen.setCellValueFactory(new PropertyValueFactory<>("denumire"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPret.setCellValueFactory(new PropertyValueFactory<>("pret"));
        colDisp.setCellValueFactory(new PropertyValueFactory<>("disponibila"));
        colDisp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : (v ? "Da" : "Nu"));
            }
        });
        tabel.getColumns().addAll(colDen, colCat, colPret, colDisp);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void reload() {
        try { date.setAll(dao.findAll()); lblStatus.setText("Meniu: " + date.size()); }
        catch (SQLException e) { err(e.getMessage()); }
    }

    private void filtreaza() {
        try {
            String t = tfCauta.getText().toLowerCase();
            String cat = cbCat.getValue();
            date.clear();
            for (Mancare m : dao.findAll()) {
                if ((t.isBlank() || m.getDenumire().toLowerCase().contains(t))
                        && (cat == null || cat.equals(m.getCategorie())))
                    date.add(m);
            }
            lblStatus.setText("Rezultate: " + date.size());
        } catch (SQLException e) { err(e.getMessage()); }
    }

    private void dialogForm(Mancare ex) {
        TextField tfDen  = new TextField(ex != null ? ex.getDenumire() : "");
        TextField tfCat  = new TextField(ex != null ? ex.getCategorie() : "");
        TextField tfPret = new TextField(ex != null ? ex.getPret().toPlainString() : "");
        CheckBox cbDisp  = new CheckBox("Disponibila"); cbDisp.setSelected(ex == null || ex.isDisponibila());
        tfDen.setPromptText("Denumire"); tfCat.setPromptText("Categorie"); tfPret.setPromptText("Pret");

        Dialog<Boolean> dlg = new Dialog<>();
        dlg.setTitle(ex == null ? "Adauga mancare" : "Editeaza mancare");
        dlg.getDialogPane().setContent(new VBox(6,
                new Label("Denumire:"), tfDen, new Label("Categorie:"), tfCat,
                new Label("Pret:"), tfPret, cbDisp));
        ButtonType ok = new ButtonType("Salveaza", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b == ok);
        dlg.showAndWait().ifPresent(confirmed -> {
            if (!confirmed) return;
            try {
                BigDecimal pret = new BigDecimal(tfPret.getText().trim().replace(",", "."));
                if (ex != null) {
                    ex.setDenumire(tfDen.getText().trim()); ex.setCategorie(tfCat.getText().trim());
                    ex.setPret(pret); ex.setDisponibila(cbDisp.isSelected());
                    dao.update(ex);
                } else {
                    dao.insert(new Mancare(0, tfDen.getText().trim(), pret,
                            tfCat.getText().trim(), cbDisp.isSelected()));
                }
                reload();
            } catch (SQLException e) { err(e.getMessage()); }
        });
    }

    private void delete() {
        Mancare sel = tabel.getSelectionModel().getSelectedItem();
        if (sel == null) { err("Selectati o mancare."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Stergeti '" + sel.getDenumire() + "'?")
            .showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                try { dao.delete(sel.getId()); reload(); } catch (SQLException e) { err(e.getMessage()); }
            });
    }

    private void err(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}