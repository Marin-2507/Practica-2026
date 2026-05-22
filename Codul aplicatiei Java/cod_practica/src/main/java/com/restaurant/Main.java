package com.restaurant;

import com.restaurant.controller.ClientiController;
import com.restaurant.controller.ComenziController;
import com.restaurant.controller.MeniuController;
import com.restaurant.controller.MeseController;
import com.restaurant.controller.RapoarteController;
import com.restaurant.dao.DatabaseManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane root;
    private Label lblStatus;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        lblStatus = new Label("Gata.");
        root.setLeft(buildSidebar());
        root.setBottom(buildStatusBar());
        root.setCenter(new Label("  Selectati o sectiune."));
        Scene scene = new Scene(root, 1050, 680);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Restaurant");
        stage.setScene(scene);
        stage.show();
        lblStatus.setText(DatabaseManager.testConnection());
    }

    private VBox buildSidebar() {
        VBox sb = new VBox(4);
        sb.getStyleClass().add("sidebar");
        sb.setPadding(new Insets(12, 8, 12, 8));
        sb.setPrefWidth(170);
        sb.getChildren().addAll(
                btn("Mese",     () -> root.setCenter(new MeseController(lblStatus))),
                btn("Meniu",    () -> root.setCenter(new MeniuController(lblStatus))),
                btn("Clienti",  () -> root.setCenter(new ClientiController(lblStatus))),
                btn("Comenzi",  () -> root.setCenter(new ComenziController(lblStatus))),
                btn("Rapoarte", () -> root.setCenter(new RapoarteController(lblStatus))),
                new Separator(),
                btn("Iesire",   () -> { DatabaseManager.close(); Platform.exit(); })
        );
        return sb;
    }

    private Button btn(String text, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add("sidebar-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> action.run());
        return b;
    }

    private HBox buildStatusBar() {
        HBox bar = new HBox(lblStatus);
        bar.getStyleClass().add("status-bar");
        bar.setPadding(new Insets(4, 10, 4, 10));
        return bar;
    }

    @Override
    public void stop() { DatabaseManager.close(); }
}