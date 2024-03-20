package com.user.main;

import java.util.Scanner;
import com.user.producteur.Producteur;

public class main {

    public static void main(String[] args) {
        Producteur producer = new Producteur("kafka:9092", "user_commands");
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            afficherMenu();
            String input = scanner.nextLine();

            // Vérifie si l'utilisateur souhaite quitter
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
                        afficherAide();
                        break;
                    default:
                        System.out.println("Commande non reconnue. Veuillez réessayer.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée non valide. Veuillez entrer un numéro.");
            }
        }

        scanner.close();
        producer.close();
        System.out.println("Producteur fermé et interface de ligne de commande fermée.");
    }

    private static void afficherMenu() {
        System.out.println("Veuillez choisir une action (tapez 'exit' pour quitter) :");
        System.out.println("1 - Afficher tous les patients");
        System.out.println("2 - Rechercher un patient par PID");
        System.out.println("3 - Rechercher un patient par nom");
        System.out.println("4 - Afficher les séjours d'un patient par PID");
        System.out.println("5 - Afficher les mouvements d'un patient par numéro de séjour");
        System.out.println("6 - Exporter les données en JSON");
        System.out.println("7 - Aide");
    }

    private static void afficherAide() {
        System.out.println("Liste des commandes :");
        System.out.println("- get_all_patients : retourne tous les patients");
        System.out.println("- get_patient_by_pid : retourne l’identité complète d’un patient par son identifiant PID-3");
        System.out.println("- get_patient_by_name : retourne l’identité d’un patient par son l’un de ses noms");
        System.out.println("- get_patient_stay_by_pid : retour les séjours d’un patient par son identifiant PID-3");
        System.out.println("- get_patient_movements_by_sid : retourne tous les mouvements d’un patient par le numéro de séjour");
        System.out.println("- Export : permet d’exporter les données de la base de données en JSON dans un fichier.");
    }
}
