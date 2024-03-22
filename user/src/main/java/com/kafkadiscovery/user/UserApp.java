package com.kafkadiscovery.user;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kafkadiscovery.user.producer.Producer;

public class UserApp {
    private static final Logger logger = Logger.getLogger(UserApp.class.getName());

    public static void main(String[] args) {
        Producer producer = new Producer("kafka:9092", "user_commands");
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            displayMenu();
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            try {
                int choix = Integer.parseInt(input);
                switch (choix) {
                    case 1:
                        // producer.sendCommand("get_all_patients");
                        break;
                    case 2:
                        // producer.sendCommand("get_patient_by_pid");
                        break;
                    case 3:
                        // producer.sendCommand("get_patient_by_name");
                        break;
                    case 4:
                        // producer.sendCommand("get_patient_stay_by_pid");
                        break;
                    case 5:
                        // producer.sendCommand("get_patient_movements_by_sid");
                        break;
                    case 6:
                        // producer.sendCommand("export_data");
                        break;
                    case 7:
                        displayHelp();
                        break;
                    default:
                        logger.log(Level.FINE,"Command not found, please try again.");
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING,"Invalid input, please try again.");
            }
        }

        scanner.close();
        producer.close();
        logger.log(Level.INFO,"Bye bye !");
    }

    private static void displayMenu() {
        String[] menuItems = {
                "Please choose an action (type 'exit' to quit):",
                "1 - Display all patients",
                "2 - Search patient by PID",
                "3 - Search patient by name",
                "4 - Display a patient's stays by PID",
                "5 - Display a patient's movements by stay number",
                "6 - Export data to JSON",
                "7 - Help"
        };

        for (String menuItem : menuItems) {
            logger.log(Level.FINE, menuItem);
        }
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
}
