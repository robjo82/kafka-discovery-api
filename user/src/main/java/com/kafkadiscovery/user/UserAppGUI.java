package com.kafkadiscovery.user;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserAppGUI extends Application {

    private static final Logger logger = Logger.getLogger(UserAppGUI.class.getName());

    @Override
    public void start(Stage primaryStage) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
                "Display all patients",
                "Search patient by PID",
                "Search patient by name",
                "Display a patient's stays by PID",
                "Display a patient's movements by stay number",
                "Export data to JSON",
                "Help"
        );
        comboBox.setOnAction(event -> {
            String selectedItem = comboBox.getValue();
            switch (selectedItem) {
                case "Display all patients":
                    // producer.sendCommand("get_all_patients");
                    break;
                case "Search patient by PID":
                    // producer.sendCommand("get_patient_by_pid");
                    break;
                case "Search patient by name":
                    // producer.sendCommand("get_patient_by_name");
                    break;
                case "Display a patient's stays by PID":
                    // producer.sendCommand("get_patient_stay_by_pid");
                    break;
                case "Display a patient's movements by stay number":
                    // producer.sendCommand("get_patient_movements_by_sid");
                    break;
                case "Export data to JSON":
                    // producer.sendCommand("export_data");
                    break;
                case "Help":
                    displayHelp();
                    break;
                default:
                    logger.log(Level.FINE,"Command not found, please try again.");
            }
        });

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().add(comboBox);

        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.setTitle("User App");
        primaryStage.show();
    }

    private static void displayHelp() {
        String[] helpItems = {
                "List of commands:",
                "- get_all_patients : returns all patients",
                "- get_patient_by_pid : returns the complete identity of a patient by his PID-3 identifier",
                "- get_patient_by_name : returns the identity of a patient by one of his names",
                "- get_patient_stay_by_pid : returns the stays of a patient by his PID-3 identifier",
                "- get_patient_movements_by_sid : returns all movements of a patient by the stay number",
                "- Export : allows to export the data from the database to JSON in a file."
        };

        for (String helpItem : helpItems) {
            logger.log(Level.FINE, helpItem);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
